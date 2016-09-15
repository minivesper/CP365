import numpy as np
import random
import math
np.random.seed(42)

def loadDataset(filename='u.data'):
    my_data = np.genfromtxt(filename)
    data = my_data[:, :3]
    # print(data)
    movies = {}
    for i in data:
        if i[1] not in movies:
            movies[i[1]] = {i[0] : i[2]}
        else:
            movies[i[1]][i[0]] = i[2]
    return movies, data

def cluster(data, cent):
    cluster = []
    for c in cent:
        cluster.append({})
    for mov, user_rat in data.iteritems():
        min_dist = float('inf')
        for c in range(len(cent)):
            dist = distance(cent[c], user_rat)
            if dist < min_dist:
                min_dist = dist
                min_cent = c
            # print(min_cent)
        cluster[min_cent][mov] = user_rat
    return cluster

def distance(p1, p2):
    dist = 0
    for uid in p1:
        if uid in p2:
            dist += (p2[uid] - p1[uid])**2
    return math.sqrt(dist)

def recalc_centroids(clust, cent):
    # calc average and assign to centroids
    for idx in range(len(clust)):
        for uid in cent[idx]:
            dist = 0
            count = 1
            for mov in clust[idx]:
                if uid in clust[idx][mov]:
                    dist += clust[idx][mov][uid]
                    count += 1

            cent[idx][uid] = dist/count

def calcError(clust, cent):
    err = 0
    for group in range(len(clust)):
        for mov in clust[group]:
            err += distance(cent[group], clust[group][mov])
    return err

def gen_cent(data, k):
    #gen random list with every userid and a random rating between 1-5
    centroids = []
    for i in range(k):
        x = {}
        for j in data:
            x[j[0]] = random.randint(0,5)
        centroids.append(x);
    return centroids

if __name__=="__main__":
    movies, raw = loadDataset()
    k = 10
    centroids = gen_cent(raw, k)
    clusters = cluster(movies, centroids)
    p_err = float('inf')
    Error = calcError(clusters, centroids)
    while (p_err - Error) > 0.001:
        clusters = cluster(movies, centroids)
        recalc_centroids(clusters, centroids)
        p_err = Error
        Error = calcError(clusters, centroids)
        print(Error)

    #print ten shortest
    shortest = []
    for idx in range(len(clusters)):
        shortest.append([])
        distances = {}
        for mov in clusters[idx]:
            distances[distance(centroids[idx], clusters[idx][mov])] = mov
        for i in range(4):
            shortest[idx].append(distances[min(distances, key=distances.get)])
            del distances[min(distances, key=distances.get)]
    print(shortest)
