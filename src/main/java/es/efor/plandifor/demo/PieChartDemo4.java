// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 

package es.efor.plandifor.demo;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.util.SortOrder;

public class PieChartDemo4 extends ApplicationFrame
{
	private static class DemoPanel extends JPanel
		implements ActionListener
	{

		JFreeChart chart;
		DefaultPieDataset dataset;
		boolean ascendingByKey;
		boolean ascendingByValue;

		public void actionPerformed(ActionEvent actionevent)
		{
			String s = actionevent.getActionCommand();
			if ("BY_KEY".equals(s))
			{
				if (!ascendingByKey)
				{
					dataset.sortByKeys(SortOrder.ASCENDING);
					ascendingByKey = true;
				} else
				{
					dataset.sortByKeys(SortOrder.DESCENDING);
					ascendingByKey = false;
				}
			} else
			if ("BY_VALUE".equals(s))
			{
				if (!ascendingByValue)
				{
					dataset.sortByValues(SortOrder.ASCENDING);
					ascendingByValue = true;
				} else
				{
					dataset.sortByValues(SortOrder.DESCENDING);
					ascendingByValue = false;
				}
			} else
			if ("RANDOM".equals(s))
			{
				ArrayList arraylist = new ArrayList(dataset.getKeys());
				Collections.shuffle(arraylist);
				DefaultPieDataset defaultpiedataset = new DefaultPieDataset();
				Comparable comparable;
				for (Iterator iterator = arraylist.iterator(); iterator.hasNext(); defaultpiedataset.setValue(comparable, dataset.getValue(comparable)))
					comparable = (Comparable)iterator.next();

				PiePlot pieplot1 = (PiePlot)chart.getPlot();
				pieplot1.setDataset(defaultpiedataset);
				dataset = defaultpiedataset;
			} else
			if ("LABELS".equals(s))
			{
				PiePlot pieplot = (PiePlot)chart.getPlot();
				boolean flag = pieplot.getSimpleLabels();
				if (flag)
				{
					pieplot.setInteriorGap(0.050000000000000003D);
					pieplot.setSimpleLabels(false);
				} else
				{
					pieplot.setInteriorGap(0.01D);
					pieplot.setSimpleLabels(true);
				}
			}
		}

		public DemoPanel(DefaultPieDataset defaultpiedataset)
		{
			super(new BorderLayout());
			ascendingByKey = false;
			ascendingByValue = false;
			dataset = defaultpiedataset;
			chart = PieChartDemo4.createChart(defaultpiedataset);
			ChartPanel chartpanel = new ChartPanel(chart);
			add(chartpanel);
			JPanel jpanel = new JPanel(new FlowLayout());
			JButton jbutton = new JButton("By Key");
			jbutton.setActionCommand("BY_KEY");
			jbutton.addActionListener(this);
			JButton jbutton1 = new JButton("By Value");
			jbutton1.setActionCommand("BY_VALUE");
			jbutton1.addActionListener(this);
			JButton jbutton2 = new JButton("Random");
			jbutton2.setActionCommand("RANDOM");
			jbutton2.addActionListener(this);
			JCheckBox jcheckbox = new JCheckBox("Simple Labels");
			jcheckbox.setActionCommand("LABELS");
			jcheckbox.addActionListener(this);
			jpanel.add(jbutton);
			jpanel.add(jbutton1);
			jpanel.add(jbutton2);
			jpanel.add(jcheckbox);
			add(jpanel, "South");
		}
	}


	public PieChartDemo4(String s)
	{
		super(s);
		JPanel jpanel = createDemoPanel();
		jpanel.setPreferredSize(new Dimension(500, 270));
		setContentPane(jpanel);
	}

	private static DefaultPieDataset createDataset()
	{
		DefaultPieDataset defaultpiedataset = new DefaultPieDataset();
		defaultpiedataset.setValue("婴幼儿童", new Double(17.200000000000003D));
		defaultpiedataset.setValue("青少年", new Double(10D));
		defaultpiedataset.setValue("中年", new Double(27.5D));
		defaultpiedataset.setValue("壮年", new Double(40.5D));
		defaultpiedataset.setValue("老年", new Double(19D));
		defaultpiedataset.setValue("其他", new Double(11.399999999999999D));
		return defaultpiedataset;
	}

	private static JFreeChart createChart(PieDataset piedataset)
	{
		JFreeChart jfreechart = ChartFactory.createPieChart("青少年人口总数占总人口比重", piedataset, true, true, false);
		PiePlot pieplot = (PiePlot)jfreechart.getPlot();
		pieplot.setLabelFont(new Font("SansSerif", 0, 12));
		pieplot.setNoDataMessage("No data available");
		pieplot.setCircular(false);
		pieplot.setLabelGap(0.02D);
		pieplot.setExplodePercent("Section D", 0.5D);
		return jfreechart;
	}

	public static JPanel createDemoPanel()
	{
		return new DemoPanel(createDataset());
	}

	public static void main(String args[])
	{
		PieChartDemo4 piechartdemo4 = new PieChartDemo4("青少年人口总数占总人口比重");
		piechartdemo4.pack();
		RefineryUtilities.centerFrameOnScreen(piechartdemo4);
		piechartdemo4.setVisible(true);
	}

}
