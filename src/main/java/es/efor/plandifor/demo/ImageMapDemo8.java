// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 

package es.efor.plandifor.demo;

import java.awt.Color;
import java.io.*;
import org.jfree.chart.*;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.entity.StandardEntityCollection;
import org.jfree.chart.imagemap.ImageMapUtilities;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.DefaultXYZDataset;
import org.jfree.data.xy.XYZDataset;

public class ImageMapDemo8
{

	public ImageMapDemo8()
	{
	}

	private static JFreeChart createChart(XYZDataset xyzdataset)
	{
		JFreeChart jfreechart = ChartFactory.createBubbleChart("Bubble Chart Demo 1", "X", "Y", xyzdataset, PlotOrientation.HORIZONTAL, true, true, true);
		XYPlot xyplot = (XYPlot)jfreechart.getPlot();
		xyplot.setForegroundAlpha(0.65F);
		XYItemRenderer xyitemrenderer = xyplot.getRenderer();
		xyitemrenderer.setSeriesPaint(0, Color.blue);
		NumberAxis numberaxis = (NumberAxis)xyplot.getDomainAxis();
		numberaxis.setLowerMargin(0.14999999999999999D);
		numberaxis.setUpperMargin(0.14999999999999999D);
		NumberAxis numberaxis1 = (NumberAxis)xyplot.getRangeAxis();
		numberaxis1.setLowerMargin(0.14999999999999999D);
		numberaxis1.setUpperMargin(0.14999999999999999D);
		return jfreechart;
	}

	public static XYZDataset createDataset()
	{
		DefaultXYZDataset defaultxyzdataset = new DefaultXYZDataset();
		double ad[] = {
			2.1000000000000001D, 2.2999999999999998D, 2.2999999999999998D, 2.2000000000000002D, 2.2000000000000002D, 1.8D, 1.8D, 1.8999999999999999D, 2.2999999999999998D, 3.7999999999999998D
		};
		double ad1[] = {
			14.1D, 11.1D, 10D, 8.8000000000000007D, 8.6999999999999993D, 8.4000000000000004D, 5.4000000000000004D, 4.0999999999999996D, 4.0999999999999996D, 25D
		};
		double ad2[] = {
			2.3999999999999999D, 2.7000000000000002D, 2.7000000000000002D, 2.2000000000000002D, 2.2000000000000002D, 2.2000000000000002D, 2.1000000000000001D, 2.2000000000000002D, 1.6000000000000001D, 4D
		};
		double ad3[][] = {
			ad, ad1, ad2
		};
		defaultxyzdataset.addSeries("Series 1", ad3);
		return defaultxyzdataset;
	}

	public static void main(String args[])
	{
		JFreeChart jfreechart = createChart(createDataset());
		try
		{
			ChartRenderingInfo chartrenderinginfo = new ChartRenderingInfo(new StandardEntityCollection());
			File file = new File("bubble100.png");
			ChartUtilities.saveChartAsPNG(file, jfreechart, 600, 400, chartrenderinginfo);
			File file1 = new File("bubble100.html");
			BufferedOutputStream bufferedoutputstream = new BufferedOutputStream(new FileOutputStream(file1));
			PrintWriter printwriter = new PrintWriter(bufferedoutputstream);
			printwriter.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\"");
			printwriter.println("\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">");
			printwriter.println("<html xmlns=\"http://www.w3.org/1999/xhtml\" lang=\"en\" xml:lang=\"en\">");
			printwriter.println("<head><title>JFreeChart Image Map Demo 8</title></head>");
			printwriter.println("<body><p>");
			ImageMapUtilities.writeImageMap(printwriter, "chart", chartrenderinginfo);
			printwriter.println("<img src=\"bubble100.png\" width=\"600\" height=\"400\" usemap=\"#chart\" alt=\"bubble100.png\"/>");
			printwriter.println("</p></body>");
			printwriter.println("</html>");
			printwriter.close();
		}
		catch (IOException ioexception)
		{
			ioexception.printStackTrace();
		}
	}
}
