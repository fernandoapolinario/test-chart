// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 

package es.efor.plandifor.demo;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JPanel;
import org.jfree.chart.*;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis3D;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.LineRenderer3D;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class DualAxisDemo4 extends ApplicationFrame
{

	public DualAxisDemo4(String s)
	{
		super(s);
		JFreeChart jfreechart = createChart();
		ChartPanel chartpanel = new ChartPanel(jfreechart);
		chartpanel.setPreferredSize(new Dimension(500, 270));
		setContentPane(chartpanel);
	}

	private static JFreeChart createChart()
	{
		CategoryDataset categorydataset = createDataset1();
		JFreeChart jfreechart = ChartFactory.createBarChart3D("Dual Axis Chart", "Category", "Value", categorydataset, PlotOrientation.VERTICAL, true, true, false);
		jfreechart.setBackgroundPaint(new Color(204, 255, 204));
		CategoryPlot categoryplot = (CategoryPlot)jfreechart.getPlot();
		categoryplot.setDomainAxisLocation(AxisLocation.BOTTOM_OR_LEFT);
		categoryplot.setRangeAxisLocation(AxisLocation.TOP_OR_LEFT);
		CategoryItemRenderer categoryitemrenderer = categoryplot.getRenderer();
		categoryitemrenderer.setSeriesPaint(0, Color.red);
		categoryitemrenderer.setSeriesPaint(1, Color.yellow);
		categoryitemrenderer.setSeriesPaint(2, Color.green);
		CategoryDataset categorydataset1 = createDataset2();
		NumberAxis3D numberaxis3d = new NumberAxis3D("Secondary");
		categoryplot.setRangeAxis(1, numberaxis3d);
		categoryplot.setDataset(1, categorydataset1);
		categoryplot.mapDatasetToRangeAxis(1, 1);
		LineRenderer3D linerenderer3d = new LineRenderer3D();
		linerenderer3d.setSeriesPaint(0, Color.blue);
		categoryplot.setRenderer(1, linerenderer3d);
		categoryplot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
		return jfreechart;
	}

	private static CategoryDataset createDataset1()
	{
		String s = "First";
		String s1 = "Second";
		String s2 = "Third";
		String s3 = "Category 1";
		String s4 = "Category 2";
		String s5 = "Category 3";
		String s6 = "Category 4";
		String s7 = "Category 5";
		DefaultCategoryDataset defaultcategorydataset = new DefaultCategoryDataset();
		defaultcategorydataset.addValue(1.0D, s, s3);
		defaultcategorydataset.addValue(4D, s, s4);
		defaultcategorydataset.addValue(3D, s, s5);
		defaultcategorydataset.addValue(5D, s, s6);
		defaultcategorydataset.addValue(5D, s, s7);
		defaultcategorydataset.addValue(5D, s1, s3);
		defaultcategorydataset.addValue(7D, s1, s4);
		defaultcategorydataset.addValue(6D, s1, s5);
		defaultcategorydataset.addValue(8D, s1, s6);
		defaultcategorydataset.addValue(4D, s1, s7);
		defaultcategorydataset.addValue(4D, s2, s3);
		defaultcategorydataset.addValue(3D, s2, s4);
		defaultcategorydataset.addValue(2D, s2, s5);
		defaultcategorydataset.addValue(3D, s2, s6);
		defaultcategorydataset.addValue(6D, s2, s7);
		return defaultcategorydataset;
	}

	private static CategoryDataset createDataset2()
	{
		String s = "Fourth";
		String s1 = "Category 1";
		String s2 = "Category 2";
		String s3 = "Category 3";
		String s4 = "Category 4";
		String s5 = "Category 5";
		DefaultCategoryDataset defaultcategorydataset = new DefaultCategoryDataset();
		defaultcategorydataset.addValue(15D, s, s1);
		defaultcategorydataset.addValue(24D, s, s2);
		defaultcategorydataset.addValue(31D, s, s3);
		defaultcategorydataset.addValue(25D, s, s4);
		defaultcategorydataset.addValue(56D, s, s5);
		return defaultcategorydataset;
	}

	public static JPanel createDemoPanel()
	{
		JFreeChart jfreechart = createChart();
		return new ChartPanel(jfreechart);
	}

	public static void main(String args[])
	{
		DualAxisDemo4 dualaxisdemo4 = new DualAxisDemo4("Dual Axis Demo 4");
		dualaxisdemo4.pack();
		RefineryUtilities.centerFrameOnScreen(dualaxisdemo4);
		dualaxisdemo4.setVisible(true);
	}
}
