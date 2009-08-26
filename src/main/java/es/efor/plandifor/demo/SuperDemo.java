// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 

package es.efor.plandifor.demo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.RefineryUtilities;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.DefaultFontMapper;
import com.lowagie.text.pdf.FontMapper;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;

import es.efor.plandifor.demo.MemoryUsageDemo.DataGenerator;

// Referenced classes of package demo:
//			MemoryUsageDemo, DemoDescription

public class SuperDemo extends ApplicationFrame
	implements ActionListener, TreeSelectionListener
{
	static class DisplayDemo
		implements Runnable
	{

		private SuperDemo app;
		private DemoDescription demoDescription;

		public void run()
		{
			try
			{
				Class class1 = Class.forName(demoDescription.getClassName());
				Method method = class1.getDeclaredMethod("createDemoPanel", null);
				JPanel jpanel = (JPanel)method.invoke(null, null);
				app.chartContainer.removeAll();
				app.chartContainer.add(jpanel);
				app.displayPanel.validate();
				String s = class1.getName();
				String s1 = s;
				int i = s.lastIndexOf('.');
				if (i > 0)
					s1 = s.substring(i + 1);
				s1 = s1 + ".html";
				app.displayDescription(s1);
			}
			catch (ClassNotFoundException classnotfoundexception)
			{
				classnotfoundexception.printStackTrace();
			}
			catch (NoSuchMethodException nosuchmethodexception)
			{
				nosuchmethodexception.printStackTrace();
			}
			catch (InvocationTargetException invocationtargetexception)
			{
				invocationtargetexception.printStackTrace();
			}
			catch (IllegalAccessException illegalaccessexception)
			{
				illegalaccessexception.printStackTrace();
			}
		}

		public DisplayDemo(SuperDemo superdemo, DemoDescription demodescription)
		{
			app = superdemo;
			demoDescription = demodescription;
		}
	}

	static class PDFExportTask
		implements Runnable
	{

		JFreeChart chart;
		int width;
		int height;
		File file;

		public void run()
		{
			try
			{
				SuperDemo.saveChartAsPDF(file, chart, width, height, new DefaultFontMapper());
			}
			catch (IOException ioexception)
			{
				ioexception.printStackTrace();
			}
		}

		public PDFExportTask(JFreeChart jfreechart, int i, int j, File file1)
		{
			chart = jfreechart;
			file = file1;
			width = i;
			height = j;
			jfreechart.setBorderVisible(true);
			jfreechart.setPadding(new RectangleInsets(2D, 2D, 2D, 2D));
		}
	}


	public static final String EXIT_COMMAND = "EXIT";
	private JPanel displayPanel;
	private JPanel chartContainer;
	private JPanel descriptionContainer;
	private JTextPane descriptionPane;

	public SuperDemo(String s)
	{
		super(s);
		setContentPane(createContent());
		setJMenuBar(createMenuBar());
	}

	private JComponent createContent()
	{
		JPanel jpanel = new JPanel(new BorderLayout());
		JTabbedPane jtabbedpane = new JTabbedPane();
		JPanel jpanel1 = new JPanel(new BorderLayout());
		jpanel1.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		JSplitPane jsplitpane = new JSplitPane(1);
		JTree jtree = new JTree(createTreeModel());
		jtree.addTreeSelectionListener(this);
		JScrollPane jscrollpane = new JScrollPane(jtree);
		jscrollpane.setPreferredSize(new Dimension(300, 100));
		jsplitpane.setLeftComponent(jscrollpane);
		jsplitpane.setRightComponent(createChartDisplayPanel());
		jpanel1.add(jsplitpane);
		jtabbedpane.add("Demos", jpanel1);
		MemoryUsageDemo memoryusagedemo = new MemoryUsageDemo(1000);
		jtabbedpane.add("Memory Usage", memoryusagedemo);
		jtabbedpane.add("Source Code", createSourceCodePanel());
		jtabbedpane.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		jpanel.add(jtabbedpane);
		return jpanel;
	}

	private JMenuBar createMenuBar()
	{
		JMenuBar jmenubar = new JMenuBar();
		JMenu jmenu = new JMenu("File", true);
		jmenu.setMnemonic('F');
		JMenuItem jmenuitem = new JMenuItem("Export to PDF...", 112);
		jmenuitem.setActionCommand("EXPORT_TO_PDF");
		jmenuitem.addActionListener(this);
		jmenu.add(jmenuitem);
		jmenu.addSeparator();
		JMenuItem jmenuitem1 = new JMenuItem("Exit", 120);
		jmenuitem1.setActionCommand("EXIT");
		jmenuitem1.addActionListener(this);
		jmenu.add(jmenuitem1);
		jmenubar.add(jmenu);
		return jmenubar;
	}

	private JPanel createSourceCodePanel()
	{
		JPanel jpanel = new JPanel(new BorderLayout());
		jpanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		JEditorPane jeditorpane = new JEditorPane();
		jeditorpane.setEditable(false);
		java.net.URL url = (es.efor.plandifor.demo.SuperDemo.class).getResource("source.html");
		if (url != null)
			try
			{
				jeditorpane.setPage(url);
			}
			catch (IOException ioexception)
			{
				System.err.println("Attempted to read a bad URL: " + url);
			}
		else
			System.err.println("Couldn't find file: source.html");
		JScrollPane jscrollpane = new JScrollPane(jeditorpane);
		jscrollpane.setVerticalScrollBarPolicy(20);
		jscrollpane.setPreferredSize(new Dimension(250, 145));
		jscrollpane.setMinimumSize(new Dimension(10, 10));
		jpanel.add(jscrollpane);
		return jpanel;
	}

	public void actionPerformed(ActionEvent actionevent)
	{
		String s = actionevent.getActionCommand();
		if (s.equals("EXPORT_TO_PDF"))
			exportToPDF();
		else
		if (s.equals("EXIT"))
			attemptExit();
	}

	private void exportToPDF()
	{
		java.awt.Component component = chartContainer.getComponent(0);
		if (component instanceof ChartPanel)
		{
			JFileChooser jfilechooser = new JFileChooser();
			jfilechooser.setName("untitled.pdf");
			jfilechooser.setFileFilter(new FileFilter() {

				public boolean accept(File file)
				{
					return file.isDirectory() || file.getName().endsWith(".pdf");
				}

				public String getDescription()
				{
					return "Portable Document Format (PDF)";
				}

			});
			int i = jfilechooser.showSaveDialog(this);
			if (i == 0)
			{
				ChartPanel chartpanel = (ChartPanel)component;
				try
				{
					JFreeChart jfreechart = (JFreeChart)chartpanel.getChart().clone();
					PDFExportTask pdfexporttask = new PDFExportTask(jfreechart, chartpanel.getWidth(), chartpanel.getHeight(), jfilechooser.getSelectedFile());
					Thread thread = new Thread(pdfexporttask);
					thread.start();
				}
				catch (CloneNotSupportedException clonenotsupportedexception)
				{
					clonenotsupportedexception.printStackTrace();
				}
			}
		} else
		{
			String s = "Unable to export the selected item.  There is ";
			s = s + "either no chart selected,\nor else the chart is not ";
			s = s + "at the expected location in the component hierarchy\n";
			s = s + "(future versions of the demo may include code to ";
			s = s + "handle these special cases).";
			JOptionPane.showMessageDialog(this, s, "PDF Export", 1);
		}
	}

	public static void writeChartAsPDF(OutputStream outputstream, JFreeChart jfreechart, int i, int j, FontMapper fontmapper)
		throws IOException
	{
		Rectangle rectangle = new Rectangle(i, j);
		Document document = new Document(rectangle, 50F, 50F, 50F, 50F);
		try
		{
			PdfWriter pdfwriter = PdfWriter.getInstance(document, outputstream);
			document.addAuthor("JFreeChart");
			document.addSubject("Demonstration");
			document.open();
			PdfContentByte pdfcontentbyte = pdfwriter.getDirectContent();
			PdfTemplate pdftemplate = pdfcontentbyte.createTemplate(i, j);
			Graphics2D graphics2d = pdftemplate.createGraphics(i, j, fontmapper);
			java.awt.geom.Rectangle2D.Double double1 = new java.awt.geom.Rectangle2D.Double(0.0D, 0.0D, i, j);
			jfreechart.draw(graphics2d, double1);
			graphics2d.dispose();
			pdfcontentbyte.addTemplate(pdftemplate, 0.0F, 0.0F);
		}
		catch (DocumentException documentexception)
		{
			System.err.println(documentexception.getMessage());
		}
		document.close();
	}

	public static void saveChartAsPDF(File file, JFreeChart jfreechart, int i, int j, FontMapper fontmapper)
		throws IOException
	{
		BufferedOutputStream bufferedoutputstream = new BufferedOutputStream(new FileOutputStream(file));
		writeChartAsPDF(bufferedoutputstream, jfreechart, i, j, fontmapper);
		bufferedoutputstream.close();
	}

	private void attemptExit()
	{
		String s = "Confirm";
		String s1 = "Are you sure you want to exit the demo?";
		int i = JOptionPane.showConfirmDialog(this, s1, s, 0, 3);
		if (i == 0)
		{
			dispose();
			System.exit(0);
		}
	}

	private JPanel createChartDisplayPanel()
	{
		displayPanel = new JPanel(new BorderLayout());
		chartContainer = new JPanel(new BorderLayout());
		chartContainer.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4), BorderFactory.createLineBorder(Color.black)));
		chartContainer.add(createNoDemoSelectedPanel());
		descriptionContainer = new JPanel(new BorderLayout());
		descriptionContainer.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		descriptionContainer.setPreferredSize(new Dimension(600, 140));
		descriptionPane = new JTextPane();
		descriptionPane.setEditable(false);
		JScrollPane jscrollpane = new JScrollPane(descriptionPane, 20, 31);
		descriptionContainer.add(jscrollpane);
		displayDescription("select.html");
		JSplitPane jsplitpane = new JSplitPane(0);
		jsplitpane.setTopComponent(chartContainer);
		jsplitpane.setBottomComponent(descriptionContainer);
		displayPanel.add(jsplitpane);
		jsplitpane.setDividerLocation(0.75D);
		return displayPanel;
	}

	private TreeModel createTreeModel()
	{
		DefaultMutableTreeNode defaultmutabletreenode = new DefaultMutableTreeNode("JFreeChart");
		defaultmutabletreenode.add(createAreaChartsNode());
		defaultmutabletreenode.add(createBarChartsNode());
		defaultmutabletreenode.add(createStackedBarChartsNode());
		defaultmutabletreenode.add(createCombinedAxisChartsNode());
		defaultmutabletreenode.add(createFinancialChartsNode());
		defaultmutabletreenode.add(createGanttChartsNode());
		defaultmutabletreenode.add(createLineChartsNode());
		defaultmutabletreenode.add(createMeterChartsNode());
		defaultmutabletreenode.add(createMultipleAxisChartsNode());
		defaultmutabletreenode.add(createOverlaidChartsNode());
		defaultmutabletreenode.add(createPieChartsNode());
		defaultmutabletreenode.add(createStatisticalChartsNode());
		defaultmutabletreenode.add(createTimeSeriesChartsNode());
		defaultmutabletreenode.add(createXYChartsNode());
		defaultmutabletreenode.add(createMiscellaneousChartsNode());
		defaultmutabletreenode.add(createExperimentalNode());
		return new DefaultTreeModel(defaultmutabletreenode);
	}

	private MutableTreeNode createPieChartsNode()
	{
		DefaultMutableTreeNode defaultmutabletreenode = new DefaultMutableTreeNode("Pie Charts");
		DefaultMutableTreeNode defaultmutabletreenode1 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.PieChartDemo1", "PieChartDemo1.java"));
		DefaultMutableTreeNode defaultmutabletreenode2 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.PieChartDemo2", "PieChartDemo2.java"));
		DefaultMutableTreeNode defaultmutabletreenode3 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.PieChartDemo3", "PieChartDemo3.java"));
		DefaultMutableTreeNode defaultmutabletreenode4 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.PieChartDemo4", "PieChartDemo4.java"));
		DefaultMutableTreeNode defaultmutabletreenode5 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.PieChartDemo5", "PieChartDemo5.java"));
		DefaultMutableTreeNode defaultmutabletreenode6 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.PieChartDemo6", "PieChartDemo6.java"));
		DefaultMutableTreeNode defaultmutabletreenode7 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.PieChartDemo7", "PieChartDemo7.java"));
		DefaultMutableTreeNode defaultmutabletreenode8 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.PieChartDemo8", "PieChartDemo8.java"));
		DefaultMutableTreeNode defaultmutabletreenode9 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.PieChart3DDemo1", "PieChart3DDemo1.java"));
		DefaultMutableTreeNode defaultmutabletreenode10 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.PieChart3DDemo2", "PieChart3DDemo2.java"));
		DefaultMutableTreeNode defaultmutabletreenode11 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.PieChart3DDemo3", "PieChart3DDemo3.java"));
		DefaultMutableTreeNode defaultmutabletreenode12 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.MultiplePieChartDemo1", "MultiplePieChartDemo1.java"));
		DefaultMutableTreeNode defaultmutabletreenode13 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.MultiplePieChartDemo2", "MultiplePieChartDemo2.java"));
		DefaultMutableTreeNode defaultmutabletreenode14 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.MultiplePieChartDemo3", "MultiplePieChartDemo3.java"));
		DefaultMutableTreeNode defaultmutabletreenode15 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.MultiplePieChartDemo4", "MultiplePieChartDemo4.java"));
		DefaultMutableTreeNode defaultmutabletreenode16 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.RingChartDemo1", "RingChartDemo1.java"));
		defaultmutabletreenode.add(defaultmutabletreenode1);
		defaultmutabletreenode.add(defaultmutabletreenode2);
		defaultmutabletreenode.add(defaultmutabletreenode3);
		defaultmutabletreenode.add(defaultmutabletreenode4);
		defaultmutabletreenode.add(defaultmutabletreenode5);
		defaultmutabletreenode.add(defaultmutabletreenode6);
		defaultmutabletreenode.add(defaultmutabletreenode7);
		defaultmutabletreenode.add(defaultmutabletreenode8);
		defaultmutabletreenode.add(defaultmutabletreenode9);
		defaultmutabletreenode.add(defaultmutabletreenode10);
		defaultmutabletreenode.add(defaultmutabletreenode11);
		defaultmutabletreenode.add(defaultmutabletreenode12);
		defaultmutabletreenode.add(defaultmutabletreenode13);
		defaultmutabletreenode.add(defaultmutabletreenode14);
		defaultmutabletreenode.add(defaultmutabletreenode15);
		defaultmutabletreenode.add(defaultmutabletreenode16);
		return defaultmutabletreenode;
	}

	private MutableTreeNode createOverlaidChartsNode()
	{
		DefaultMutableTreeNode defaultmutabletreenode = new DefaultMutableTreeNode("Overlaid Charts");
		DefaultMutableTreeNode defaultmutabletreenode1 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.OverlaidBarChartDemo1", "OverlaidBarChartDemo1.java"));
		DefaultMutableTreeNode defaultmutabletreenode2 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.OverlaidBarChartDemo2", "OverlaidBarChartDemo2.java"));
		DefaultMutableTreeNode defaultmutabletreenode3 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.OverlaidXYPlotDemo1", "OverlaidXYPlotDemo1.java"));
		DefaultMutableTreeNode defaultmutabletreenode4 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.OverlaidXYPlotDemo2", "OverlaidXYPlotDemo2.java"));
		defaultmutabletreenode.add(defaultmutabletreenode1);
		defaultmutabletreenode.add(defaultmutabletreenode2);
		defaultmutabletreenode.add(defaultmutabletreenode3);
		defaultmutabletreenode.add(defaultmutabletreenode4);
		return defaultmutabletreenode;
	}

	private MutableTreeNode createBarChartsNode()
	{
		DefaultMutableTreeNode defaultmutabletreenode = new DefaultMutableTreeNode("Bar Charts");
		defaultmutabletreenode.add(createCategoryBarChartsNode());
		defaultmutabletreenode.add(createXYBarChartsNode());
		return defaultmutabletreenode;
	}

	private MutableTreeNode createStackedBarChartsNode()
	{
		DefaultMutableTreeNode defaultmutabletreenode = new DefaultMutableTreeNode("Bar Charts - Stacked");
		DefaultMutableTreeNode defaultmutabletreenode1 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.StackedBarChartDemo1", "StackedBarChartDemo1.java"));
		DefaultMutableTreeNode defaultmutabletreenode2 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.StackedBarChartDemo2", "StackedBarChartDemo2.java"));
		DefaultMutableTreeNode defaultmutabletreenode3 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.StackedBarChartDemo3", "StackedBarChartDemo3.java"));
		DefaultMutableTreeNode defaultmutabletreenode4 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.StackedBarChartDemo4", "StackedBarChartDemo4.java"));
		DefaultMutableTreeNode defaultmutabletreenode5 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.StackedBarChartDemo5", "StackedBarChartDemo5.java"));
		DefaultMutableTreeNode defaultmutabletreenode6 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.StackedBarChartDemo6", "StackedBarChartDemo6.java"));
		DefaultMutableTreeNode defaultmutabletreenode7 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.StackedBarChartDemo7", "StackedBarChartDemo7.java"));
		DefaultMutableTreeNode defaultmutabletreenode8 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.StackedBarChart3DDemo1", "StackedBarChart3DDemo1.java"));
		DefaultMutableTreeNode defaultmutabletreenode9 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.StackedBarChart3DDemo2", "StackedBarChart3DDemo2.java"));
		DefaultMutableTreeNode defaultmutabletreenode10 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.StackedBarChart3DDemo3", "StackedBarChart3DDemo3.java"));
		DefaultMutableTreeNode defaultmutabletreenode11 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.StackedBarChart3DDemo4", "StackedBarChart3DDemo4.java"));
		DefaultMutableTreeNode defaultmutabletreenode12 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.StackedBarChart3DDemo5", "StackedBarChart3DDemo5.java"));
		defaultmutabletreenode.add(defaultmutabletreenode1);
		defaultmutabletreenode.add(defaultmutabletreenode2);
		defaultmutabletreenode.add(defaultmutabletreenode3);
		defaultmutabletreenode.add(defaultmutabletreenode4);
		defaultmutabletreenode.add(defaultmutabletreenode5);
		defaultmutabletreenode.add(defaultmutabletreenode6);
		defaultmutabletreenode.add(defaultmutabletreenode7);
		defaultmutabletreenode.add(defaultmutabletreenode8);
		defaultmutabletreenode.add(defaultmutabletreenode9);
		defaultmutabletreenode.add(defaultmutabletreenode10);
		defaultmutabletreenode.add(defaultmutabletreenode11);
		defaultmutabletreenode.add(defaultmutabletreenode12);
		return defaultmutabletreenode;
	}

	private MutableTreeNode createCategoryBarChartsNode()
	{
		DefaultMutableTreeNode defaultmutabletreenode = new DefaultMutableTreeNode("CategoryPlot");
		DefaultMutableTreeNode defaultmutabletreenode1 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.BarChartDemo1", "BarChartDemo1.java"));
		DefaultMutableTreeNode defaultmutabletreenode2 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.BarChartDemo2", "BarChartDemo2.java"));
		DefaultMutableTreeNode defaultmutabletreenode3 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.BarChartDemo3", "BarChartDemo3.java"));
		DefaultMutableTreeNode defaultmutabletreenode4 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.BarChartDemo4", "BarChartDemo4.java"));
		DefaultMutableTreeNode defaultmutabletreenode5 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.BarChartDemo5", "BarChartDemo5.java"));
		DefaultMutableTreeNode defaultmutabletreenode6 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.BarChartDemo6", "BarChartDemo6.java"));
		DefaultMutableTreeNode defaultmutabletreenode7 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.BarChartDemo7", "BarChartDemo7.java"));
		DefaultMutableTreeNode defaultmutabletreenode8 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.BarChartDemo8", "BarChartDemo8.java"));
		DefaultMutableTreeNode defaultmutabletreenode9 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.BarChartDemo9", "BarChartDemo9.java"));
		DefaultMutableTreeNode defaultmutabletreenode10 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.BarChartDemo10", "BarChartDemo10.java"));
		DefaultMutableTreeNode defaultmutabletreenode11 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.BarChart3DDemo1", "BarChart3DDemo1.java"));
		DefaultMutableTreeNode defaultmutabletreenode12 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.BarChart3DDemo2", "BarChart3DDemo2.java"));
		DefaultMutableTreeNode defaultmutabletreenode13 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.BarChart3DDemo3", "BarChart3DDemo3.java"));
		DefaultMutableTreeNode defaultmutabletreenode14 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.BarChart3DDemo4", "BarChart3DDemo4.java"));
		DefaultMutableTreeNode defaultmutabletreenode15 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.CylinderChartDemo1", "CylinderChartDemo1.java"));
		DefaultMutableTreeNode defaultmutabletreenode16 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.CylinderChartDemo2", "CylinderChartDemo2.java"));
		DefaultMutableTreeNode defaultmutabletreenode17 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.IntervalBarChartDemo1", "IntervalBarChartDemo1.java"));
		DefaultMutableTreeNode defaultmutabletreenode18 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.LayeredBarChartDemo1", "LayeredBarChartDemo1.java"));
		DefaultMutableTreeNode defaultmutabletreenode19 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.LayeredBarChartDemo2", "LayeredBarChartDemo2.java"));
		DefaultMutableTreeNode defaultmutabletreenode20 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.StatisticalBarChartDemo1", "StatisticalBarChartDemo1.java"));
		DefaultMutableTreeNode defaultmutabletreenode21 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.SurveyResultsDemo1", "SurveyResultsDemo1.java"));
		DefaultMutableTreeNode defaultmutabletreenode22 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.SurveyResultsDemo2", "SurveyResultsDemo2.java"));
		DefaultMutableTreeNode defaultmutabletreenode23 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.SurveyResultsDemo3", "SurveyResultsDemo3.java"));
		DefaultMutableTreeNode defaultmutabletreenode24 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.WaterfallChartDemo1", "WaterfallChartDemo1.java"));
		defaultmutabletreenode.add(defaultmutabletreenode1);
		defaultmutabletreenode.add(defaultmutabletreenode2);
		defaultmutabletreenode.add(defaultmutabletreenode3);
		defaultmutabletreenode.add(defaultmutabletreenode4);
		defaultmutabletreenode.add(defaultmutabletreenode5);
		defaultmutabletreenode.add(defaultmutabletreenode6);
		defaultmutabletreenode.add(defaultmutabletreenode7);
		defaultmutabletreenode.add(defaultmutabletreenode8);
		defaultmutabletreenode.add(defaultmutabletreenode9);
		defaultmutabletreenode.add(defaultmutabletreenode10);
		defaultmutabletreenode.add(defaultmutabletreenode11);
		defaultmutabletreenode.add(defaultmutabletreenode12);
		defaultmutabletreenode.add(defaultmutabletreenode13);
		defaultmutabletreenode.add(defaultmutabletreenode14);
		defaultmutabletreenode.add(defaultmutabletreenode15);
		defaultmutabletreenode.add(defaultmutabletreenode16);
		defaultmutabletreenode.add(defaultmutabletreenode17);
		defaultmutabletreenode.add(defaultmutabletreenode18);
		defaultmutabletreenode.add(defaultmutabletreenode19);
		defaultmutabletreenode.add(defaultmutabletreenode20);
		defaultmutabletreenode.add(defaultmutabletreenode21);
		defaultmutabletreenode.add(defaultmutabletreenode22);
		defaultmutabletreenode.add(defaultmutabletreenode23);
		defaultmutabletreenode.add(defaultmutabletreenode24);
		return defaultmutabletreenode;
	}

	private MutableTreeNode createXYBarChartsNode()
	{
		DefaultMutableTreeNode defaultmutabletreenode = new DefaultMutableTreeNode("XYPlot");
		DefaultMutableTreeNode defaultmutabletreenode1 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.XYBarChartDemo1", "XYBarChartDemo1.java"));
		DefaultMutableTreeNode defaultmutabletreenode2 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.XYBarChartDemo2", "XYBarChartDemo2.java"));
		DefaultMutableTreeNode defaultmutabletreenode3 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.XYBarChartDemo3", "XYBarChartDemo3.java"));
		DefaultMutableTreeNode defaultmutabletreenode4 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.XYBarChartDemo4", "XYBarChartDemo4.java"));
		DefaultMutableTreeNode defaultmutabletreenode5 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.XYBarChartDemo5", "XYBarChartDemo5.java"));
		DefaultMutableTreeNode defaultmutabletreenode6 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.XYBarChartDemo6", "XYBarChartDemo6.java"));
		DefaultMutableTreeNode defaultmutabletreenode7 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.XYBarChartDemo7", "XYBarChartDemo7.java"));
		DefaultMutableTreeNode defaultmutabletreenode8 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.ClusteredXYBarRendererDemo1", "ClusteredXYBarRendererDemo1.java"));
		DefaultMutableTreeNode defaultmutabletreenode9 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.StackedXYBarChartDemo1", "StackedXYBarChartDemo1.java"));
		DefaultMutableTreeNode defaultmutabletreenode10 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.StackedXYBarChartDemo2", "StackedXYBarChartDemo2.java"));
		DefaultMutableTreeNode defaultmutabletreenode11 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.StackedXYBarChartDemo3", "StackedXYBarChartDemo3.java"));
		DefaultMutableTreeNode defaultmutabletreenode12 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.RelativeDateFormatDemo1", "RelativeDateFormatDemo1.java"));
		DefaultMutableTreeNode defaultmutabletreenode13 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.RelativeDateFormatDemo2", "RelativeDateFormatDemo2.java"));
		defaultmutabletreenode.add(defaultmutabletreenode1);
		defaultmutabletreenode.add(defaultmutabletreenode2);
		defaultmutabletreenode.add(defaultmutabletreenode3);
		defaultmutabletreenode.add(defaultmutabletreenode4);
		defaultmutabletreenode.add(defaultmutabletreenode5);
		defaultmutabletreenode.add(defaultmutabletreenode6);
		defaultmutabletreenode.add(defaultmutabletreenode7);
		defaultmutabletreenode.add(defaultmutabletreenode8);
		defaultmutabletreenode.add(defaultmutabletreenode9);
		defaultmutabletreenode.add(defaultmutabletreenode10);
		defaultmutabletreenode.add(defaultmutabletreenode11);
		defaultmutabletreenode.add(defaultmutabletreenode12);
		defaultmutabletreenode.add(defaultmutabletreenode13);
		return defaultmutabletreenode;
	}

	private MutableTreeNode createLineChartsNode()
	{
		DefaultMutableTreeNode defaultmutabletreenode = new DefaultMutableTreeNode("Line Charts");
		DefaultMutableTreeNode defaultmutabletreenode1 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.AnnotationDemo1", "AnnotationDemo1.java"));
		DefaultMutableTreeNode defaultmutabletreenode2 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.LineChartDemo1", "LineChartDemo1.java"));
		DefaultMutableTreeNode defaultmutabletreenode3 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.LineChartDemo2", "LineChartDemo2.java"));
		DefaultMutableTreeNode defaultmutabletreenode4 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.LineChartDemo3", "LineChartDemo3.java"));
		DefaultMutableTreeNode defaultmutabletreenode5 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.LineChartDemo4", "LineChartDemo4.java"));
		DefaultMutableTreeNode defaultmutabletreenode6 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.LineChartDemo5", "LineChartDemo5.java"));
		DefaultMutableTreeNode defaultmutabletreenode7 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.LineChartDemo6", "LineChartDemo6.java"));
		DefaultMutableTreeNode defaultmutabletreenode8 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.LineChartDemo7", "LineChartDemo7.java"));
		DefaultMutableTreeNode defaultmutabletreenode9 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.LineChartDemo8", "LineChartDemo8.java"));
		DefaultMutableTreeNode defaultmutabletreenode10 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.LineChart3DDemo1", "LineChart3DDemo1.java"));
		DefaultMutableTreeNode defaultmutabletreenode11 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.StatisticalLineChartDemo1", "StatisticalLineChartDemo1.java"));
		DefaultMutableTreeNode defaultmutabletreenode12 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.XYSplineRendererDemo1", "XYSplineRendererDemo1.java"));
		DefaultMutableTreeNode defaultmutabletreenode13 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.XYStepRendererDemo1", "XYStepRendererDemo1.java"));
		DefaultMutableTreeNode defaultmutabletreenode14 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.XYStepRendererDemo2", "XYStepRendererDemo2.java"));
		defaultmutabletreenode.add(defaultmutabletreenode1);
		defaultmutabletreenode.add(defaultmutabletreenode2);
		defaultmutabletreenode.add(defaultmutabletreenode3);
		defaultmutabletreenode.add(defaultmutabletreenode4);
		defaultmutabletreenode.add(defaultmutabletreenode5);
		defaultmutabletreenode.add(defaultmutabletreenode6);
		defaultmutabletreenode.add(defaultmutabletreenode7);
		defaultmutabletreenode.add(defaultmutabletreenode8);
		defaultmutabletreenode.add(defaultmutabletreenode9);
		defaultmutabletreenode.add(defaultmutabletreenode10);
		defaultmutabletreenode.add(defaultmutabletreenode11);
		defaultmutabletreenode.add(defaultmutabletreenode12);
		defaultmutabletreenode.add(defaultmutabletreenode13);
		defaultmutabletreenode.add(defaultmutabletreenode14);
		return defaultmutabletreenode;
	}

	private MutableTreeNode createAreaChartsNode()
	{
		DefaultMutableTreeNode defaultmutabletreenode = new DefaultMutableTreeNode("Area Charts");
		DefaultMutableTreeNode defaultmutabletreenode1 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.AreaChartDemo1", "AreaChartDemo1.java"));
		DefaultMutableTreeNode defaultmutabletreenode2 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.StackedXYAreaChartDemo1", "StackedXYAreaChartDemo1.java"));
		DefaultMutableTreeNode defaultmutabletreenode3 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.StackedXYAreaChartDemo2", "StackedXYAreaChartDemo2.java"));
		DefaultMutableTreeNode defaultmutabletreenode4 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.XYAreaChartDemo1", "XYAreaChartDemo1.java"));
		DefaultMutableTreeNode defaultmutabletreenode5 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.XYAreaChartDemo2", "XYAreaChartDemo2.java"));
		DefaultMutableTreeNode defaultmutabletreenode6 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.XYStepAreaRendererDemo1", "XYStepAreaRendererDemo1.java"));
		defaultmutabletreenode.add(defaultmutabletreenode1);
		defaultmutabletreenode.add(defaultmutabletreenode2);
		defaultmutabletreenode.add(defaultmutabletreenode3);
		defaultmutabletreenode.add(defaultmutabletreenode4);
		defaultmutabletreenode.add(defaultmutabletreenode5);
		defaultmutabletreenode.add(defaultmutabletreenode6);
		return defaultmutabletreenode;
	}

	private MutableTreeNode createStatisticalChartsNode()
	{
		DefaultMutableTreeNode defaultmutabletreenode = new DefaultMutableTreeNode("Statistical Charts");
		DefaultMutableTreeNode defaultmutabletreenode1 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.BoxAndWhiskerChartDemo1", "BoxAndWhiskerChartDemo1.java"));
		DefaultMutableTreeNode defaultmutabletreenode2 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.HistogramDemo1", "HistogramDemo1.java"));
		DefaultMutableTreeNode defaultmutabletreenode3 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.MinMaxCategoryPlotDemo1", "MinMaxCategoryPlotDemo1.java"));
		DefaultMutableTreeNode defaultmutabletreenode4 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.NormalDistributionDemo1", "NormalDistributionDemo1.java"));
		DefaultMutableTreeNode defaultmutabletreenode5 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.RegressionDemo1", "RegressionDemo1.java"));
		DefaultMutableTreeNode defaultmutabletreenode6 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.ScatterPlotDemo1", "ScatterPlotDemo1.java"));
		DefaultMutableTreeNode defaultmutabletreenode7 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.ScatterPlotDemo2", "ScatterPlotDemo2.java"));
		DefaultMutableTreeNode defaultmutabletreenode8 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.ScatterPlotDemo3", "ScatterPlotDemo3.java"));
		DefaultMutableTreeNode defaultmutabletreenode9 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.ScatterPlotDemo4", "ScatterPlotDemo4.java"));
		DefaultMutableTreeNode defaultmutabletreenode10 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.XYErrorRendererDemo1", "XYErrorRendererDemo1.java"));
		defaultmutabletreenode.add(defaultmutabletreenode1);
		defaultmutabletreenode.add(defaultmutabletreenode2);
		defaultmutabletreenode.add(defaultmutabletreenode3);
		defaultmutabletreenode.add(defaultmutabletreenode4);
		defaultmutabletreenode.add(defaultmutabletreenode5);
		defaultmutabletreenode.add(defaultmutabletreenode6);
		defaultmutabletreenode.add(defaultmutabletreenode7);
		defaultmutabletreenode.add(defaultmutabletreenode8);
		defaultmutabletreenode.add(defaultmutabletreenode9);
		defaultmutabletreenode.add(defaultmutabletreenode10);
		return defaultmutabletreenode;
	}

	private MutableTreeNode createTimeSeriesChartsNode()
	{
		DefaultMutableTreeNode defaultmutabletreenode = new DefaultMutableTreeNode("Time Series Charts");
		DefaultMutableTreeNode defaultmutabletreenode1 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.PeriodAxisDemo1", "PeriodAxisDemo1.java"));
		DefaultMutableTreeNode defaultmutabletreenode2 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.PeriodAxisDemo2", "PeriodAxisDemo2.java"));
		DefaultMutableTreeNode defaultmutabletreenode3 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.RelativeDateFormatDemo1", "RelativeDateFormatDemo1.java"));
		DefaultMutableTreeNode defaultmutabletreenode4 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.TimeSeriesDemo1", "TimeSeriesDemo1.java"));
		DefaultMutableTreeNode defaultmutabletreenode5 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.TimeSeriesDemo2", "TimeSeriesDemo2.java"));
		DefaultMutableTreeNode defaultmutabletreenode6 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.TimeSeriesDemo3", "TimeSeriesDemo3.java"));
		DefaultMutableTreeNode defaultmutabletreenode7 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.TimeSeriesDemo4", "TimeSeriesDemo4.java"));
		DefaultMutableTreeNode defaultmutabletreenode8 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.TimeSeriesDemo5", "TimeSeriesDemo5.java"));
		DefaultMutableTreeNode defaultmutabletreenode9 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.TimeSeriesDemo6", "TimeSeriesDemo6.java"));
		DefaultMutableTreeNode defaultmutabletreenode10 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.TimeSeriesDemo7", "TimeSeriesDemo7.java"));
		DefaultMutableTreeNode defaultmutabletreenode11 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.TimeSeriesDemo8", "TimeSeriesDemo8.java"));
		DefaultMutableTreeNode defaultmutabletreenode12 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.TimeSeriesDemo9", "TimeSeriesDemo9.java"));
		DefaultMutableTreeNode defaultmutabletreenode13 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.TimeSeriesDemo10", "TimeSeriesDemo10.java"));
		DefaultMutableTreeNode defaultmutabletreenode14 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.TimeSeriesDemo11", "TimeSeriesDemo11.java"));
		DefaultMutableTreeNode defaultmutabletreenode15 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.TimeSeriesDemo12", "TimeSeriesDemo12.java"));
		DefaultMutableTreeNode defaultmutabletreenode16 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.TimeSeriesDemo13", "TimeSeriesDemo13.java"));
		DefaultMutableTreeNode defaultmutabletreenode17 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.DeviationRendererDemo1", "DeviationRendererDemo1.java"));
		DefaultMutableTreeNode defaultmutabletreenode18 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.DeviationRendererDemo2", "DeviationRendererDemo2.java"));
		defaultmutabletreenode.add(defaultmutabletreenode1);
		defaultmutabletreenode.add(defaultmutabletreenode2);
		defaultmutabletreenode.add(defaultmutabletreenode3);
		defaultmutabletreenode.add(defaultmutabletreenode4);
		defaultmutabletreenode.add(defaultmutabletreenode5);
		defaultmutabletreenode.add(defaultmutabletreenode6);
		defaultmutabletreenode.add(defaultmutabletreenode7);
		defaultmutabletreenode.add(defaultmutabletreenode8);
		defaultmutabletreenode.add(defaultmutabletreenode9);
		defaultmutabletreenode.add(defaultmutabletreenode10);
		defaultmutabletreenode.add(defaultmutabletreenode11);
		defaultmutabletreenode.add(defaultmutabletreenode12);
		defaultmutabletreenode.add(defaultmutabletreenode13);
		defaultmutabletreenode.add(defaultmutabletreenode14);
		defaultmutabletreenode.add(defaultmutabletreenode15);
		defaultmutabletreenode.add(defaultmutabletreenode16);
		defaultmutabletreenode.add(defaultmutabletreenode17);
		defaultmutabletreenode.add(defaultmutabletreenode18);
		return defaultmutabletreenode;
	}

	private MutableTreeNode createFinancialChartsNode()
	{
		DefaultMutableTreeNode defaultmutabletreenode = new DefaultMutableTreeNode("Financial Charts");
		DefaultMutableTreeNode defaultmutabletreenode1 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.CandlestickChartDemo1", "CandlestickChartDemo1.java"));
		DefaultMutableTreeNode defaultmutabletreenode2 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.HighLowChartDemo1", "HighLowChartDemo1.java"));
		DefaultMutableTreeNode defaultmutabletreenode3 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.HighLowChartDemo2", "HighLowChartDemo2.java"));
		DefaultMutableTreeNode defaultmutabletreenode4 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.PriceVolumeDemo1", "PriceVolumeDemo1.java"));
		DefaultMutableTreeNode defaultmutabletreenode5 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.YieldCurveDemo", "YieldCurveDemo.java"));
		defaultmutabletreenode.add(defaultmutabletreenode1);
		defaultmutabletreenode.add(defaultmutabletreenode2);
		defaultmutabletreenode.add(defaultmutabletreenode3);
		defaultmutabletreenode.add(defaultmutabletreenode4);
		defaultmutabletreenode.add(defaultmutabletreenode5);
		return defaultmutabletreenode;
	}

	private MutableTreeNode createXYChartsNode()
	{
		DefaultMutableTreeNode defaultmutabletreenode = new DefaultMutableTreeNode("XY Charts");
		DefaultMutableTreeNode defaultmutabletreenode1 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.ScatterPlotDemo1", "ScatterPlotDemo1.java"));
		DefaultMutableTreeNode defaultmutabletreenode2 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.ScatterPlotDemo2", "ScatterPlotDemo2.java"));
		DefaultMutableTreeNode defaultmutabletreenode3 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.ScatterPlotDemo3", "ScatterPlotDemo3.java"));
		DefaultMutableTreeNode defaultmutabletreenode4 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.XYBlockChartDemo1", "XYBlockChartDemo1.java"));
		DefaultMutableTreeNode defaultmutabletreenode5 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.XYBlockChartDemo2", "XYBlockChartDemo2.java"));
		DefaultMutableTreeNode defaultmutabletreenode6 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.XYBlockChartDemo3", "XYBlockChartDemo3.java"));
		DefaultMutableTreeNode defaultmutabletreenode7 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.XYLineAndShapeRendererDemo1", "XYLineAndShapeRendererDemo1.java"));
		DefaultMutableTreeNode defaultmutabletreenode8 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.XYLineAndShapeRendererDemo2", "XYLineAndShapeRendererDemo2.java"));
		DefaultMutableTreeNode defaultmutabletreenode9 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.XYSeriesDemo1", "XYSeriesDemo1.java"));
		DefaultMutableTreeNode defaultmutabletreenode10 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.XYSeriesDemo2", "XYSeriesDemo2.java"));
		DefaultMutableTreeNode defaultmutabletreenode11 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.XYSeriesDemo3", "XYSeriesDemo3.java"));
		DefaultMutableTreeNode defaultmutabletreenode12 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.VectorPlotDemo1", "VectorPlotDemo1.java"));
		DefaultMutableTreeNode defaultmutabletreenode13 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.WindChartDemo1", "WindChartDemo1.java"));
		defaultmutabletreenode.add(defaultmutabletreenode1);
		defaultmutabletreenode.add(defaultmutabletreenode2);
		defaultmutabletreenode.add(defaultmutabletreenode3);
		defaultmutabletreenode.add(defaultmutabletreenode4);
		defaultmutabletreenode.add(defaultmutabletreenode5);
		defaultmutabletreenode.add(defaultmutabletreenode6);
		defaultmutabletreenode.add(defaultmutabletreenode7);
		defaultmutabletreenode.add(defaultmutabletreenode8);
		defaultmutabletreenode.add(defaultmutabletreenode9);
		defaultmutabletreenode.add(defaultmutabletreenode10);
		defaultmutabletreenode.add(defaultmutabletreenode11);
		defaultmutabletreenode.add(defaultmutabletreenode12);
		defaultmutabletreenode.add(defaultmutabletreenode13);
		return defaultmutabletreenode;
	}

	private MutableTreeNode createMeterChartsNode()
	{
		DefaultMutableTreeNode defaultmutabletreenode = new DefaultMutableTreeNode("Dial / Meter Charts");
		DefaultMutableTreeNode defaultmutabletreenode1 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.DialDemo1", "DialDemo1.java"));
		DefaultMutableTreeNode defaultmutabletreenode2 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.DialDemo2", "DialDemo2.java"));
		DefaultMutableTreeNode defaultmutabletreenode3 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.DialDemo3", "DialDemo3.java"));
		DefaultMutableTreeNode defaultmutabletreenode4 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.DialDemo4", "DialDemo4.java"));
		DefaultMutableTreeNode defaultmutabletreenode5 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.DialDemo5", "DialDemo5.java"));
		DefaultMutableTreeNode defaultmutabletreenode6 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.MeterChartDemo1", "MeterChartDemo1.java"));
		DefaultMutableTreeNode defaultmutabletreenode7 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.MeterChartDemo2", "MeterChartDemo2.java"));
		DefaultMutableTreeNode defaultmutabletreenode8 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.MeterChartDemo3", "MeterChartDemo3.java"));
		DefaultMutableTreeNode defaultmutabletreenode9 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.ThermometerDemo1", "ThermometerDemo1.java"));
		defaultmutabletreenode.add(defaultmutabletreenode1);
		defaultmutabletreenode.add(defaultmutabletreenode2);
		defaultmutabletreenode.add(defaultmutabletreenode3);
		defaultmutabletreenode.add(defaultmutabletreenode4);
		defaultmutabletreenode.add(defaultmutabletreenode5);
		defaultmutabletreenode.add(defaultmutabletreenode6);
		defaultmutabletreenode.add(defaultmutabletreenode7);
		defaultmutabletreenode.add(defaultmutabletreenode8);
		defaultmutabletreenode.add(defaultmutabletreenode9);
		return defaultmutabletreenode;
	}

	private MutableTreeNode createMultipleAxisChartsNode()
	{
		DefaultMutableTreeNode defaultmutabletreenode = new DefaultMutableTreeNode("Multiple Axis Charts");
		DefaultMutableTreeNode defaultmutabletreenode1 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.DualAxisDemo1", "DualAxisDemo1.java"));
		DefaultMutableTreeNode defaultmutabletreenode2 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.DualAxisDemo2", "DualAxisDemo2.java"));
		DefaultMutableTreeNode defaultmutabletreenode3 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.DualAxisDemo3", "DualAxisDemo3.java"));
		DefaultMutableTreeNode defaultmutabletreenode4 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.DualAxisDemo4", "DualAxisDemo4.java"));
		DefaultMutableTreeNode defaultmutabletreenode5 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.DualAxisDemo5", "DualAxisDemo5.java"));
		DefaultMutableTreeNode defaultmutabletreenode6 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.MultipleAxisDemo1", "MultipleAxisDemo1.java"));
		DefaultMutableTreeNode defaultmutabletreenode7 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.MultipleAxisDemo2", "MultipleAxisDemo2.java"));
		DefaultMutableTreeNode defaultmutabletreenode8 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.MultipleAxisDemo3", "MultipleAxisDemo3.java"));
		DefaultMutableTreeNode defaultmutabletreenode9 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.ParetoChartDemo1", "ParetoChartDemo1.java"));
		defaultmutabletreenode.add(defaultmutabletreenode1);
		defaultmutabletreenode.add(defaultmutabletreenode2);
		defaultmutabletreenode.add(defaultmutabletreenode3);
		defaultmutabletreenode.add(defaultmutabletreenode4);
		defaultmutabletreenode.add(defaultmutabletreenode5);
		defaultmutabletreenode.add(defaultmutabletreenode6);
		defaultmutabletreenode.add(defaultmutabletreenode7);
		defaultmutabletreenode.add(defaultmutabletreenode8);
		defaultmutabletreenode.add(defaultmutabletreenode9);
		return defaultmutabletreenode;
	}

	private MutableTreeNode createCombinedAxisChartsNode()
	{
		DefaultMutableTreeNode defaultmutabletreenode = new DefaultMutableTreeNode("Combined Axis Charts");
		DefaultMutableTreeNode defaultmutabletreenode1 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.CombinedCategoryPlotDemo1", "CombinedCategoryPlotDemo1.java"));
		DefaultMutableTreeNode defaultmutabletreenode2 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.CombinedCategoryPlotDemo2", "CombinedCategoryPlotDemo2.java"));
		DefaultMutableTreeNode defaultmutabletreenode3 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.CombinedTimeSeriesDemo1", "CombinedTimeSeriesDemo1.java"));
		DefaultMutableTreeNode defaultmutabletreenode4 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.CombinedXYPlotDemo1", "CombinedXYPlotDemo1.java"));
		DefaultMutableTreeNode defaultmutabletreenode5 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.CombinedXYPlotDemo2", "CombinedXYPlotDemo2.java"));
		DefaultMutableTreeNode defaultmutabletreenode6 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.CombinedXYPlotDemo3", "CombinedXYPlotDemo3.java"));
		DefaultMutableTreeNode defaultmutabletreenode7 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.CombinedXYPlotDemo4", "CombinedXYPlotDemo4.java"));
		defaultmutabletreenode.add(defaultmutabletreenode1);
		defaultmutabletreenode.add(defaultmutabletreenode2);
		defaultmutabletreenode.add(defaultmutabletreenode3);
		defaultmutabletreenode.add(defaultmutabletreenode4);
		defaultmutabletreenode.add(defaultmutabletreenode5);
		defaultmutabletreenode.add(defaultmutabletreenode6);
		defaultmutabletreenode.add(defaultmutabletreenode7);
		return defaultmutabletreenode;
	}

	private MutableTreeNode createGanttChartsNode()
	{
		DefaultMutableTreeNode defaultmutabletreenode = new DefaultMutableTreeNode("Gantt Charts");
		DefaultMutableTreeNode defaultmutabletreenode1 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.GanttDemo1", "GanttDemo1.java"));
		DefaultMutableTreeNode defaultmutabletreenode2 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.GanttDemo2", "GanttDemo2.java"));
		defaultmutabletreenode.add(defaultmutabletreenode1);
		defaultmutabletreenode.add(defaultmutabletreenode2);
		return defaultmutabletreenode;
	}

	private MutableTreeNode createMiscellaneousChartsNode()
	{
		DefaultMutableTreeNode defaultmutabletreenode = new DefaultMutableTreeNode("Miscellaneous");
		DefaultMutableTreeNode defaultmutabletreenode1 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.BubbleChartDemo1", "BubbleChartDemo1.java"));
		DefaultMutableTreeNode defaultmutabletreenode2 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.BubbleChartDemo2", "BubbleChartDemo2.java"));
		DefaultMutableTreeNode defaultmutabletreenode3 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.CategoryLabelPositionsDemo1", "CategoryLabelPositionsDemo1.java"));
		DefaultMutableTreeNode defaultmutabletreenode4 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.CategoryStepChartDemo1", "CategoryStepChartDemo1.java"));
		DefaultMutableTreeNode defaultmutabletreenode5 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.CompassDemo1", "CompassDemo1.java"));
		DefaultMutableTreeNode defaultmutabletreenode6 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.CompassFormatDemo1", "CompassFormatDemo1.java"));
		DefaultMutableTreeNode defaultmutabletreenode7 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.CompassFormatDemo2", "CompassFormatDemo2.java"));
		DefaultMutableTreeNode defaultmutabletreenode8 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.DifferenceChartDemo1", "DifferenceChartDemo1.java"));
		DefaultMutableTreeNode defaultmutabletreenode9 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.DifferenceChartDemo2", "DifferenceChartDemo2.java"));
		DefaultMutableTreeNode defaultmutabletreenode10 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.EventFrequencyDemo1", "EventFrequencyDemo1.java"));
		DefaultMutableTreeNode defaultmutabletreenode11 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.GradientPaintTransformerDemo1", "GradientPaintTransformerDemo1.java"));
		DefaultMutableTreeNode defaultmutabletreenode12 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.GridBandDemo1", "GridBandDemo1.java"));
		DefaultMutableTreeNode defaultmutabletreenode13 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.HideSeriesDemo1", "HideSeriesDemo1.java"));
		DefaultMutableTreeNode defaultmutabletreenode14 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.HideSeriesDemo2", "HideSeriesDemo2.java"));
		DefaultMutableTreeNode defaultmutabletreenode15 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.HideSeriesDemo3", "HideSeriesDemo3.java"));
		DefaultMutableTreeNode defaultmutabletreenode16 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.MultipleDatasetDemo1", "MultipleDatasetDemo1.java"));
		DefaultMutableTreeNode defaultmutabletreenode17 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.PolarChartDemo1", "PolarChartDemo1.java"));
		DefaultMutableTreeNode defaultmutabletreenode18 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.ScatterRendererDemo1", "ScatterRendererDemo1.java"));
		DefaultMutableTreeNode defaultmutabletreenode19 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.SpiderWebChartDemo1", "SpiderWebChartDemo1.java"));
		DefaultMutableTreeNode defaultmutabletreenode20 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.SymbolAxisDemo1", "SymbolAxisDemo1.java"));
		DefaultMutableTreeNode defaultmutabletreenode21 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.ThermometerDemo1", "ThermometerDemo1.java"));
		DefaultMutableTreeNode defaultmutabletreenode22 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.ThermometerDemo2", "ThermometerDemo2.java"));
		DefaultMutableTreeNode defaultmutabletreenode23 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.TranslateDemo1", "TranslateDemo1.java"));
		DefaultMutableTreeNode defaultmutabletreenode24 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.YIntervalChartDemo1", "YIntervalChartDemo1.java"));
		defaultmutabletreenode.add(createAnnotationsNode());
		defaultmutabletreenode.add(createCrosshairChartsNode());
		defaultmutabletreenode.add(createDynamicChartsNode());
		defaultmutabletreenode.add(createItemLabelsNode());
		defaultmutabletreenode.add(createLegendNode());
		defaultmutabletreenode.add(createMarkersNode());
		defaultmutabletreenode.add(createOrientationNode());
		defaultmutabletreenode.add(defaultmutabletreenode1);
		defaultmutabletreenode.add(defaultmutabletreenode2);
		defaultmutabletreenode.add(defaultmutabletreenode3);
		defaultmutabletreenode.add(defaultmutabletreenode4);
		defaultmutabletreenode.add(defaultmutabletreenode5);
		defaultmutabletreenode.add(defaultmutabletreenode6);
		defaultmutabletreenode.add(defaultmutabletreenode7);
		defaultmutabletreenode.add(defaultmutabletreenode8);
		defaultmutabletreenode.add(defaultmutabletreenode9);
		defaultmutabletreenode.add(defaultmutabletreenode10);
		defaultmutabletreenode.add(defaultmutabletreenode11);
		defaultmutabletreenode.add(defaultmutabletreenode12);
		defaultmutabletreenode.add(defaultmutabletreenode13);
		defaultmutabletreenode.add(defaultmutabletreenode14);
		defaultmutabletreenode.add(defaultmutabletreenode15);
		defaultmutabletreenode.add(defaultmutabletreenode16);
		defaultmutabletreenode.add(defaultmutabletreenode17);
		defaultmutabletreenode.add(defaultmutabletreenode18);
		defaultmutabletreenode.add(defaultmutabletreenode19);
		defaultmutabletreenode.add(defaultmutabletreenode20);
		defaultmutabletreenode.add(defaultmutabletreenode21);
		defaultmutabletreenode.add(defaultmutabletreenode22);
		defaultmutabletreenode.add(defaultmutabletreenode23);
		defaultmutabletreenode.add(defaultmutabletreenode24);
		return defaultmutabletreenode;
	}

	private MutableTreeNode createAnnotationsNode()
	{
		DefaultMutableTreeNode defaultmutabletreenode = new DefaultMutableTreeNode("Annotations");
		DefaultMutableTreeNode defaultmutabletreenode1 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.AnnotationDemo1", "AnnotationDemo1.java"));
		DefaultMutableTreeNode defaultmutabletreenode2 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.AnnotationDemo2", "AnnotationDemo2.java"));
		DefaultMutableTreeNode defaultmutabletreenode3 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.CategoryPointerAnnotationDemo1", "CategoryPointerAnnotationDemo1.java"));
		DefaultMutableTreeNode defaultmutabletreenode4 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.XYBoxAnnotationDemo1", "XYBoxAnnotationDemo1.java"));
		DefaultMutableTreeNode defaultmutabletreenode5 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.XYPolygonAnnotationDemo1", "XYPolygonAnnotationDemo1.java"));
		defaultmutabletreenode.add(defaultmutabletreenode1);
		defaultmutabletreenode.add(defaultmutabletreenode2);
		defaultmutabletreenode.add(defaultmutabletreenode3);
		defaultmutabletreenode.add(defaultmutabletreenode4);
		defaultmutabletreenode.add(defaultmutabletreenode5);
		return defaultmutabletreenode;
	}

	private MutableTreeNode createCrosshairChartsNode()
	{
		DefaultMutableTreeNode defaultmutabletreenode = new DefaultMutableTreeNode("Crosshairs");
		DefaultMutableTreeNode defaultmutabletreenode1 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.CrosshairDemo1", "CrosshairDemo1.java"));
		DefaultMutableTreeNode defaultmutabletreenode2 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.CrosshairDemo2", "CrosshairDemo2.java"));
		DefaultMutableTreeNode defaultmutabletreenode3 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.CrosshairDemo3", "CrosshairDemo3.java"));
		DefaultMutableTreeNode defaultmutabletreenode4 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.CrosshairDemo4", "CrosshairDemo4.java"));
		defaultmutabletreenode.add(defaultmutabletreenode1);
		defaultmutabletreenode.add(defaultmutabletreenode2);
		defaultmutabletreenode.add(defaultmutabletreenode3);
		defaultmutabletreenode.add(defaultmutabletreenode4);
		return defaultmutabletreenode;
	}

	private MutableTreeNode createDynamicChartsNode()
	{
		DefaultMutableTreeNode defaultmutabletreenode = new DefaultMutableTreeNode("Dynamic Charts");
		DefaultMutableTreeNode defaultmutabletreenode1 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.DynamicDataDemo1", "DynamicDataDemo1.java"));
		DefaultMutableTreeNode defaultmutabletreenode2 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.DynamicDataDemo2", "DynamicDataDemo2.java"));
		DefaultMutableTreeNode defaultmutabletreenode3 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.DynamicDataDemo3", "DynamicDataDemo3.java"));
		DefaultMutableTreeNode defaultmutabletreenode4 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.MouseOverDemo1", "MouseOverDemo1.java"));
		defaultmutabletreenode.add(defaultmutabletreenode1);
		defaultmutabletreenode.add(defaultmutabletreenode2);
		defaultmutabletreenode.add(defaultmutabletreenode3);
		defaultmutabletreenode.add(defaultmutabletreenode4);
		return defaultmutabletreenode;
	}

	private MutableTreeNode createItemLabelsNode()
	{
		DefaultMutableTreeNode defaultmutabletreenode = new DefaultMutableTreeNode("Item Labels");
		DefaultMutableTreeNode defaultmutabletreenode1 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.ItemLabelDemo1", "ItemLabelDemo1.java"));
		DefaultMutableTreeNode defaultmutabletreenode2 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.ItemLabelDemo2", "ItemLabelDemo2.java"));
		DefaultMutableTreeNode defaultmutabletreenode3 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.ItemLabelDemo3", "ItemLabelDemo3.java"));
		DefaultMutableTreeNode defaultmutabletreenode4 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.ItemLabelDemo4", "ItemLabelDemo4.java"));
		DefaultMutableTreeNode defaultmutabletreenode5 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.ItemLabelDemo5", "ItemLabelDemo5.java"));
		defaultmutabletreenode.add(defaultmutabletreenode1);
		defaultmutabletreenode.add(defaultmutabletreenode2);
		defaultmutabletreenode.add(defaultmutabletreenode3);
		defaultmutabletreenode.add(defaultmutabletreenode4);
		defaultmutabletreenode.add(defaultmutabletreenode5);
		return defaultmutabletreenode;
	}

	private MutableTreeNode createLegendNode()
	{
		DefaultMutableTreeNode defaultmutabletreenode = new DefaultMutableTreeNode("Legends");
		DefaultMutableTreeNode defaultmutabletreenode1 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.LegendWrapperDemo1", "LegendWrapperDemo1.java"));
		defaultmutabletreenode.add(defaultmutabletreenode1);
		return defaultmutabletreenode;
	}

	private MutableTreeNode createMarkersNode()
	{
		DefaultMutableTreeNode defaultmutabletreenode = new DefaultMutableTreeNode("Markers");
		DefaultMutableTreeNode defaultmutabletreenode1 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.CategoryMarkerDemo1", "CategoryMarkerDemo1.java"));
		DefaultMutableTreeNode defaultmutabletreenode2 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.CategoryMarkerDemo2", "CategoryMarkerDemo2.java"));
		DefaultMutableTreeNode defaultmutabletreenode3 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.MarkerDemo1", "MarkerDemo1.java"));
		DefaultMutableTreeNode defaultmutabletreenode4 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.MarkerDemo2", "MarkerDemo2.java"));
		defaultmutabletreenode.add(defaultmutabletreenode1);
		defaultmutabletreenode.add(defaultmutabletreenode2);
		defaultmutabletreenode.add(defaultmutabletreenode3);
		defaultmutabletreenode.add(defaultmutabletreenode4);
		return defaultmutabletreenode;
	}

	private MutableTreeNode createOrientationNode()
	{
		DefaultMutableTreeNode defaultmutabletreenode = new DefaultMutableTreeNode("Plot Orientation");
		DefaultMutableTreeNode defaultmutabletreenode1 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.PlotOrientationDemo1", "PlotOrientationDemo1.java"));
		DefaultMutableTreeNode defaultmutabletreenode2 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.PlotOrientationDemo2", "PlotOrientationDemo2.java"));
		defaultmutabletreenode.add(defaultmutabletreenode1);
		defaultmutabletreenode.add(defaultmutabletreenode2);
		return defaultmutabletreenode;
	}

	private MutableTreeNode createExperimentalNode()
	{
		DefaultMutableTreeNode defaultmutabletreenode = new DefaultMutableTreeNode("Experimental");
		DefaultMutableTreeNode defaultmutabletreenode1 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.LogAxisDemo1", "LogAxisDemo1.java"));
		DefaultMutableTreeNode defaultmutabletreenode2 = new DefaultMutableTreeNode(new DemoDescription("es.efor.plandifor.demo.experimental.XYTitleAnnotationDemo1", "XYTitleAnnotationDemo1.java"));
		defaultmutabletreenode.add(defaultmutabletreenode1);
		defaultmutabletreenode.add(defaultmutabletreenode2);
		return defaultmutabletreenode;
	}

	private void displayDescription(String s)
	{
		java.net.URL url = (es.efor.plandifor.demo.SuperDemo.class).getResource(s);
		if (url != null)
			try
			{
				descriptionPane.setPage(url);
			}
			catch (IOException ioexception)
			{
				System.err.println("Attempted to read a bad URL: " + url);
			}
		else
			System.err.println("Couldn't find file: " + s);
	}

	public void valueChanged(TreeSelectionEvent treeselectionevent)
	{
		TreePath treepath = treeselectionevent.getPath();
		Object obj = treepath.getLastPathComponent();
		if (obj != null)
		{
			DefaultMutableTreeNode defaultmutabletreenode = (DefaultMutableTreeNode)obj;
			Object obj1 = defaultmutabletreenode.getUserObject();
			if (obj1 instanceof DemoDescription)
			{
				DemoDescription demodescription = (DemoDescription)obj1;
				SwingUtilities.invokeLater(new DisplayDemo(this, demodescription));
			} else
			{
				chartContainer.removeAll();
				chartContainer.add(createNoDemoSelectedPanel());
				displayPanel.validate();
				displayDescription("select.html");
			}
		}
		System.out.println(obj);
	}

	private JPanel createNoDemoSelectedPanel()
	{
		JPanel jpanel = new JPanel(new FlowLayout()) {

			public String getToolTipText()
			{
				return "(" + getWidth() + ", " + getHeight() + ")";
			}

		};
		ToolTipManager.sharedInstance().registerComponent(jpanel);
		jpanel.add(new JLabel("No demo selected"));
		jpanel.setPreferredSize(new Dimension(600, 400));
		return jpanel;
	}

	public static void main(String args[])
	{
		SuperDemo superdemo = new SuperDemo("JFreeChart 1.0.9 Demo Collection");
		superdemo.pack();
		RefineryUtilities.centerFrameOnScreen(superdemo);
		superdemo.setVisible(true);
	}



}
