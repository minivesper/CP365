import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.geom.*;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.*;
import java.util.ArrayList;
import java.lang.InterruptedException;
import java.util.Random;

// Each MyPolygon has a color and a Polygon object
class MyPolygon {

	Polygon polygon;
	Color color;

	public MyPolygon(Polygon _p, Color _c) {
		polygon = _p;
		color = _c;
	}

	public Color getColor() {
		return color;
	}

	public Polygon getPolygon() {
		return polygon;
	}

}


// Each GASolution has a list of MyPolygon objects
class GASolution {

	ArrayList<MyPolygon> shapes;

	// width and height are for the full resulting image
	int width, height;

	public GASolution(int _width, int _height) {
		shapes = new ArrayList<MyPolygon>();
		width = _width;
		height = _height;
	}

	public void addPolygon(MyPolygon p) {
		shapes.add(p);
	}

	public ArrayList<MyPolygon> getShapes() {
		return shapes;
	}

	public int size() {
		return shapes.size();
	}

	// Create a BufferedImage of this solution
	// Use this to compare an evolved solution with
	// a BufferedImage of the target image
	//
	// This is almost surely NOT the fastest way to do this...
	public BufferedImage getImage() {
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		for (MyPolygon p : shapes) {
			Graphics g2 = image.getGraphics();
			g2.setColor(p.getColor());
			Polygon poly = p.getPolygon();
			if (poly.npoints > 0) {
				g2.fillPolygon(poly);
			}
		}
		return image;
	}

	public String toString() {
		return "" + shapes;
	}
}


// A Canvas to draw the highest ranked solution each epoch
class GACanvas extends JComponent{

    int width, height;
    GASolution solution;

    public GACanvas(int WINDOW_WIDTH, int WINDOW_HEIGHT) {
    	width = WINDOW_WIDTH;
    	height = WINDOW_HEIGHT;
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public void setImage(GASolution sol) {
  	    solution = sol;
    }

    public void paintComponent(Graphics g) {
		BufferedImage image = solution.getImage();
		g.drawImage(image, 0, 0, null);
    }
}


public class GA extends JComponent{

    GACanvas canvas;
    int width, height;
    BufferedImage realPicture;
    ArrayList<GASolution> population;

    // Adjust these parameters as necessary for your simulation
    double MUTATION_RATE = 0.01;
    double CROSSOVER_RATE = 0.6;
    int MAX_POLYGON_POINTS = 5;
    int MAX_POLYGONS = 10 ;

    public GA(GACanvas _canvas, BufferedImage _realPicture) {
        canvas = _canvas;
        realPicture = _realPicture;
        width = realPicture.getWidth();
        height = realPicture.getHeight();
        population = new ArrayList<GASolution>();

        // You'll need to define the following functions
        createPopulation(100);	// Make 100 new, random chromosomes
    }

		public void createPopulation(int pop_size)
		{
				for(int i = 0; i < pop_size; i++)
				{
					GASolution sol = new GASolution(width, height);
					int numPols = 0;
					while(numPols < MAX_POLYGONS)
					{
						// System.out.println("added shape");
						Polygon shape = new Polygon();
						Color c = new Color((int)(Math.random()*255), (int)(Math.random()*255), (int)(Math.random()*255));
						int numPois = 0;
						while(numPois < MAX_POLYGON_POINTS)
						{
							// System.out.println("added point");
							shape.addPoint((int)(Math.random()*width), (int)(Math.random()*height));
							numPois++;
						}
						MyPolygon polygon = new MyPolygon(shape, c);
						numPols++;
						sol.addPolygon(polygon);
					}
					this.population.add(sol);
				}
				//generate random polygons
		}

		public float fitness(BufferedImage target, BufferedImage test)
		{
			int fitness = 0;
			for( int i = 0; i < 100; i++)
			{
				//chooses 100 image points and calculates distance between that point on target and generation image
				Color tar_c = new Color(target.getRGB((int)(Math.random()*this.width), (int)(Math.random()*this.height)));
				Color tes_c = new Color(test.getRGB((int)(Math.random()*this.width), (int)(Math.random()*this.height)));
				fitness += Math.sqrt(Math.pow(tar_c.getRed() - tes_c.getRed(),2) + Math.pow(tar_c.getGreen() - tes_c.getGreen(),2) + Math.pow(tar_c.getBlue() - tes_c.getBlue(),2));
			}
			// System.out.println("fit: " + 1/((float)(fitness/100))  );
			return 1/((float)(fitness/100));
		}

		public GASolution mutate(GASolution target)
		{
			//gets shapes
			ArrayList<MyPolygon> tar_shapes = target.getShapes();
			for(int i = 0; i < tar_shapes.size(); i++)
			{
				Random r = new Random();
				for(int j = 0; j < MAX_POLYGON_POINTS; j++) //tar_shapes.get(i).polygon.xpoints.length
				{
					tar_shapes.get(i).polygon.xpoints[j] += r.nextInt(50) - 25;
					tar_shapes.get(i).polygon.xpoints[j] = min_max(tar_shapes.get(i).polygon.xpoints[j], this.width);
					tar_shapes.get(i).polygon.ypoints[j] += r.nextInt(50) - 25;
					tar_shapes.get(i).polygon.ypoints[j] = min_max(tar_shapes.get(i).polygon.ypoints[j], this.height);
					// System.out.println("x: " + tar_shapes.get(i).polygon.xpoints[j] + " y: " + tar_shapes.get(i).polygon.ypoints[j]);
				}
				tar_shapes.get(i).color = new Color(
				min_max(tar_shapes.get(i).getColor().getRed() + r.nextInt(20) - 10, 255),
				min_max(tar_shapes.get(i).getColor().getBlue() + r.nextInt(20) - 10, 255),
				min_max(tar_shapes.get(i).getColor().getGreen() + r.nextInt(20) - 10, 255));
			}
			return target;
			//should I mutate color
		}

		public int min_max(int num, int max)
		{
			if(num < max)
			{
				if(num < 0)
				{
					return 0;
				}
				else
				{
					return num;
				}
			}
			else
			{
				return max;
			}
		}

		public GASolution crossOver(GASolution parent1, GASolution parent2)
		{
			//generates empty child and gets shape arrays
			GASolution childSol = new GASolution(width, height);
			ArrayList<MyPolygon> par1_shapes = parent1.getShapes();
			ArrayList<MyPolygon> par2_shapes = parent2.getShapes();

			//loops through shapes, deep copies five shapes from par1 five from par2
			int d = (int)(Math.random()*par1_shapes.size());
			for(int i = 0; i < par1_shapes.size(); i++)
			{
			int[] xps;
			int[] yps;
			int p_col;
				if(i < d)
				{
					 xps = par1_shapes.get(i).polygon.xpoints.clone();
					 yps = par1_shapes.get(i).polygon.ypoints.clone();
					 p_col = par1_shapes.get(i).color.getRGB();
				}
				else
				{
					 xps = par2_shapes.get(i).polygon.xpoints.clone();
					 yps = par2_shapes.get(i).polygon.ypoints.clone();
					 p_col = par2_shapes.get(i).color.getRGB();
				}

				//generates obects
				Color c = new Color(p_col);
				Polygon poly =  new Polygon(xps, yps, xps.length);
				MyPolygon p = new MyPolygon(poly, c);
				childSol.addPolygon(p);
			}
			// ArrayList<MyPolygon> child_s = childSol.getShapes();
			// System.out.println(child_s.get(4).polygon.xpoints[6]);
			// System.out.println(child_s.get(4).polygon.ypoints[6]);
			//check points
			return childSol;
		}

		// public void setSolution(GASolution target, GASolution otherSol)
		// {
		// 	ArrayList<MyPolygon> tar_shapes = target.getShapes();
		// 	tar_shapes.clear();
		// 	ArrayList<MyPolygon> other_shapes = otherSol.getShapes();
		// 	for(int i = 0; i < other_shapes.size(); i++)
		// 	{
		// 		int[] xps = new int[other_shapes.get(i).polygon.xpoints.length];
		// 		int[] yps = new int[other_shapes.get(i).polygon.ypoints.length];
		// 		for(int j = 0; j < other_shapes.get(i).polygon.xpoints.length; j++)
		// 		{
		// 			xps[j] = other_shapes.get(i).polygon.xpoints[j];
		// 			yps[j] = other_shapes.get(i).polygon.ypoints[j];
		// 		}
		// 		Color c = new Color(other_shapes.get(i).color.getRGB());
		// 		Polygon poly =  new Polygon(xps, yps, xps.length);
		// 		MyPolygon p = new MyPolygon(poly, c);
		// 		// System.out.println(p);
		// 		target.addPolygon(p);
		// 	}
		// }

		public GASolution pickFitIndividual(ArrayList<Float> fitnesses)
		{
			float sum_fitness = 0;
			for(int i = 0; i < this.population.size(); i++)
			{
				sum_fitness += fitnesses.get(i);
			}
			double x = Math.random() * sum_fitness;
			// System.out.println(x);
			int ind = -1;
			while(x >= 0 && ind < (this.population.size() - 2))
			{
				ind++;
				x -= fitnesses.get(ind);
				// System.out.println(ind);
			}
			return this.population.get(ind);
		}

		public void testPickFit(ArrayList<Float> fits)
		{
			float avg = 0;
			for(int i = 0; i < fits.size(); i++)
			{
				 avg += fits.get(i);
				//  System.out.print(fits.get(i) + " ");
			}
			System.out.println("rand: " + (float)(avg/fits.size()));
			float oavg = 0;
			for( int i = 0; i < 50; i++)
			{
				oavg += fitness(this.realPicture, pickFitIndividual(fits).getImage());
			}
			System.out.println("pick fit: " + (Float)(oavg/50));
		}

    public void runSimulation()
		{
			int epoch = 10000;
			for( int j = 0; j < epoch; j++)
			{
				//generate new pop and fitness array, +assorted
				ArrayList<GASolution> new_pop = new ArrayList<GASolution>();
				ArrayList<Float> fitnesses = new ArrayList<Float>();
				GASolution max = this.population.get(0);
				float avg_fit = 0;
				float max_fit = 0;
				//one behind ok?

				//populate fitness array, also set max fit from prev population
				for(int i = 0; i < this.population.size(); i++)
				{
					float fit = fitness(this.realPicture, this.population.get(i).getImage());
					avg_fit += fit;
					if(fit > max_fit)
					{
						max_fit = fit;
						max = this.population.get(i);
					}
					fitnesses.add(fit);
				}

				testPickFit(fitnesses);

				//do crossover and mutate, if crossover not chosen, just grabs one from pickfit
				int cross = 0;
				int mutate = 0;
				for( int i = 0; i < this.population.size(); i++)
				{
					double d = Math.random();
					if(d < CROSSOVER_RATE)
					{
						cross++;
						GASolution child_sol = crossOver(pickFitIndividual(fitnesses),pickFitIndividual(fitnesses));
						d = Math.random();
						if(d < MUTATION_RATE)
						{
							mutate++;
							mutate(child_sol);
						}
						new_pop.add(child_sol);
					}
					else
					{
						new_pop.add(pickFitIndividual(fitnesses));
					}
				}
				this.population = new_pop;
				if( j % 10 == 0)
				{
					System.out.println("cross: " + cross);
					System.out.println("mutate: " + mutate);
					System.out.println("epoch: " + j + " avg fit: " +  1/(avg_fit/fitnesses.size()));
					canvas.setImage(max);
					canvas.repaint();
				}
			}
    }

    public static void main(String[] args) throws IOException {

        String realPictureFilename = "test.jpg";
        BufferedImage realPicture = ImageIO.read(new File(realPictureFilename));

        JFrame frame = new JFrame();
        frame.setSize(realPicture.getWidth(), realPicture.getHeight());
        frame.setTitle("GA Simulation of Art");

				GASolution empty = new GASolution(realPicture.getWidth(), realPicture.getHeight());
        GACanvas theCanvas = new GACanvas(realPicture.getWidth(), realPicture.getHeight());
				theCanvas.setImage(empty);
        frame.add(theCanvas);
        frame.setVisible(true);

        GA pt = new GA(theCanvas, realPicture);
        pt.runSimulation();
    }
}
