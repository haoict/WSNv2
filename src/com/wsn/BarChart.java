package com.wsn;

import java.awt.Color;
import java.util.ArrayList;
import javax.swing.JFrame;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 * This demo shows how to create a dual axis bar chart. A workaround is used
 * because the {@link BarRenderer} and {@link CategoryAxis} classes will overlap
 * the bars for the two datasets - to get around this, an an additional series
 * (containing 'null' values) is added to each dataset, and the getLegendItems()
 * method in the plot is overridden.
 *
 */
public class BarChart extends JFrame {

    /**
     * Creates a new demo instance.
     *
     * @param title the frame title.
     * @param records
     * @param isTime
     * @param isDistance
     */
    public BarChart(final String title, ArrayList<Record> records, boolean isTime, boolean isDistance) {
        super(title);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        final CategoryDataset dataTime = createDataset1(records, isTime);
        final CategoryDataset dataDistance = createDataset2(records, isDistance);
        final JFreeChart chart = createChart(dataTime, dataDistance);
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(chartPanel);
    }

    // ****************************************************************************
    // * JFREECHART DEVELOPER GUIDE                                               *
    // * The JFreeChart Developer Guide, written by David Gilbert, is available   *
    // * to purchase from Object Refinery Limited:                                *
    // *                                                                          *
    // * http://www.object-refinery.com/jfreechart/guide.html                     *
    // *                                                                          *
    // * Sales are used to provide funding for the JFreeChart project - please    * 
    // * support us so that we can continue developing free software.             *
    // ****************************************************************************
    /**
     * Creates a sample dataset.
     *
     * @return The dataset.
     */
    private CategoryDataset createDataset1(ArrayList<Record> records, boolean isTime) {

        // row keys...
        final String series1 = "Time";
        final String series2 = "Dummy 1";

        // create the dataset...
        final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int i = 0; i < records.size(); i++) {
            if (isTime) {
                dataset.addValue(records.get(i).timeF - records.get(i).timeS, series1, ((Integer) i).toString());
            } else {
                dataset.addValue(null, series1, ((Integer) i).toString());
            }
            dataset.addValue(null, series2, ((Integer) i).toString());
        }
        return dataset;

    }

    /**
     * Creates a sample dataset.
     *
     * @return The dataset.
     */
    private CategoryDataset createDataset2(ArrayList<Record> records, boolean isDistance) {

        // row keys...
        final String series1 = "Dummy 2";
        final String series2 = "Distance";

        final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int i = 0; i < records.size(); i++) {
            dataset.addValue(null, series1, ((Integer) i).toString());
            if (isDistance) {
                dataset.addValue(records.get(i).dis, series2, ((Integer) i).toString());
            } else {
                dataset.addValue(null, series2, ((Integer) i).toString());
            }

        }

        return dataset;

    }

    /**
     * Creates a chart.
     *
     * @param dataset1 the first dataset.
     * @param dataset2 the second dataset.
     *
     * @return A chart.
     */
    private JFreeChart createChart(final CategoryDataset dataset1, final CategoryDataset dataset2) {

        final CategoryAxis domainAxis = new CategoryAxis("Message");
        final NumberAxis rangeAxis = new NumberAxis("Time");
        final BarRenderer renderer1 = new BarRenderer();
        final CategoryPlot plot = new CategoryPlot(dataset1, domainAxis, rangeAxis, renderer1) {
            /**
             * Override the getLegendItems() method to handle special case.
             *
             * @return the legend items.
             */
            @Override
            public LegendItemCollection getLegendItems() {

                final LegendItemCollection result = new LegendItemCollection();

                final CategoryDataset data = getDataset();
                if (data != null) {
                    final CategoryItemRenderer r = getRenderer();
                    if (r != null) {
                        final LegendItem item = r.getLegendItem(0, 0);
                        result.add(item);
                    }
                }

                // the JDK 1.2.2 compiler complained about the name of this
                // variable 
                final CategoryDataset dset2 = getDataset(1);
                if (dset2 != null) {
                    final CategoryItemRenderer renderer2 = getRenderer(1);
                    if (renderer2 != null) {
                        final LegendItem item = renderer2.getLegendItem(1, 1);
                        result.add(item);
                    }
                }

                return result;

            }
        };

        final JFreeChart chart = new JFreeChart("Dual Axis Bar Chart", plot);
        chart.setBackgroundPaint(Color.white);
//        chart.getLegend().setAnchor(Legend.SOUTH);
        plot.setBackgroundPaint(new Color(0xEE, 0xEE, 0xFF));
        plot.setDomainAxisLocation(AxisLocation.BOTTOM_OR_RIGHT);
        plot.setDataset(1, dataset2);
        plot.mapDatasetToRangeAxis(1, 1);
        final ValueAxis axis2 = new NumberAxis("Distance");
        plot.setRangeAxis(1, axis2);
        plot.setRangeAxisLocation(1, AxisLocation.BOTTOM_OR_RIGHT);
        final BarRenderer renderer2 = new BarRenderer();
        plot.setRenderer(1, renderer2);

        return chart;
    }
}