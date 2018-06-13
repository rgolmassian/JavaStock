

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.function.LineFunction2D;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.statistics.Regression;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;


import com.github.axet.wget.WGet;

public class Sandbox {
	 
	//-------------------------------------Scraping Methods-----------------------------------------
	
	/**
	 * This goes to the URL in the first argument, and saves that webpage as the file name given
	 * @param url_to_download  the actual URL you want to download
	 * @param file_name_path   the path and file name to save the download as
	 * @return
	 */
	public static boolean download_URL(String url_to_download, String file_name_path)
	{ //utility method to allow download of internet resources, files, html pages, images, etc...
		//       returns boolean indicating whether the download was successful or not
		//usage: String url = "http://www.darkskywest.com/arizona-milky-way-spiral-meets-labyrinth-spir.jpg";
		//    String file_name_path = "C:\\Users\\Orion\\Desktop";
		//    boolean success = download_file(url, file_name_path);
		try
		{
			//internet url (ftp, http)
			URL url = new URL(url_to_download);
			// target folder or filename
			File target = new File(file_name_path);
			
			//if that file exists, delete it first
			if(target.exists())
				target.delete();
			
			// initialize wget object
			WGet w = new WGet(url, target);
			// single thread download. will return here only when file download
			// is complete (or error raised).
			w.download();
			return true;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("File Download Error");
			return false;
		} 
	}

	
	/**
	 * Looks in specified text to match a designated pattern. Will either return all matches (only group portion) found or just first match found.
	 * @param regex_pattern the pattern you want to look for
	 * @param text_to_mine the 
	 * @param all_matches
	 * @return
	 */
	/*
	* Method: Looks in specified text to match a designated pattern. Will either return all matches found or just first match found.
	* Returns: ArrayList of type String containing matches found
	* Arguments:
	*   String regex_pattern: The pattern to look for in text_to_mine.
	*      Visit https://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html to find symbols to parse data. 
	*      EX: regex_pattern "\\d+(\\.\\d+)?" will find any number in text_to_mine, either integer or decimal
	*   String text_to_mine: String of text to look for the indicated pattern
	*   boolean all_matches: Indicate "true" for returned ArrayList to contain all matches found. "False" will return ArrayList of length 1 the contains only first match found.
	*/
	public static String[] easy_regex(String regex_pattern, String text_to_mine, boolean all_matches){
		//based on boolean (true = all matches, false = first one)
		Pattern oRegex = Pattern.compile(regex_pattern, Pattern.CASE_INSENSITIVE);
		Matcher match = oRegex.matcher(text_to_mine);

		ArrayList<String> oMatchCollection = new ArrayList<String>();
		while (match.find()) {
			oMatchCollection.add(match.group(1));
		}

		if(!all_matches && !oMatchCollection.isEmpty()) {
			oMatchCollection.subList(1, oMatchCollection.size()).clear();
		}

		//modify the Arraylist back to an array
		String[] converted = oMatchCollection.toArray(new String[oMatchCollection.size()]);
		return converted;

	}

	/**
	 * Looks in specified text to match a designated pattern. Will either return all matches found (entire match) or just first match found.
	 * @param regex_pattern the pattern you want to look for
	 * @param text_to_mine the 
	 * @param all_matches
	 * @return
	 */
	/*
	* Method: Looks in specified text to match a designated pattern. Will either return all matches found or just first match found.
	* Returns: ArrayList of type String containing matches found
	* Arguments:
	*   String regex_pattern: The pattern to look for in text_to_mine.
	*      Visit https://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html to find symbols to parse data. 
	*      EX: regex_pattern "\\d+(\\.\\d+)?" will find any number in text_to_mine, either integer or decimal
	*   String text_to_mine: String of text to look for the indicated pattern
	*   boolean all_matches: Indicate "true" for returned ArrayList to contain all matches found. "False" will return ArrayList of length 1 the contains only first match found.
	*/
	public static String[] easy_regex_full(String regex_pattern, String text_to_mine, boolean all_matches){
		//based on boolean (true = all matches, false = first one)
		Pattern oRegex = Pattern.compile(regex_pattern, Pattern.CASE_INSENSITIVE);
		Matcher match = oRegex.matcher(text_to_mine);

		ArrayList<String> oMatchCollection = new ArrayList<String>();
		while (match.find()) {
			oMatchCollection.add(match.group());
		}

		if(!all_matches && !oMatchCollection.isEmpty()) {
			oMatchCollection.subList(1, oMatchCollection.size()).clear();
		}

		//modify the Arraylist back to an array
		String[] converted = oMatchCollection.toArray(new String[oMatchCollection.size()]);
		return converted;

	}

	
	
	//------------------------------------File IO Methods-------------------------------------------
	
	/**
	 * Goes to the file specified and reads the entire contents, returned as a String
	 * @param file_path_name  where the file to read is, including the file name
	 * @return
	 */
	public static String read_file_into_string(String file_path_name){
		
		String file_guts = "";
		try {
			file_guts = new String(Files.readAllBytes(Paths.get(file_path_name)), StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		} 

		return file_guts;
	}
	
	
	
	
	//-------------------------------------JFreeChart Methods-----------------------------------	
	
	/*
	   * Method: Create a scatter plot chart
	   * Returns: Nothing, but graph will appear on screen
	   * Arguments:
	   *    String title: Title of chart
	   *    String x_axis_label: Label along x-axis
	   *    String y_axis_label: Label along y-axis
	   *    String data_label: Name of data
	   *    double[] x_values: All x coordinate values
	   *    double[] y_values: All y coordinate values
	   *       (x_values[0], y_values[0]) = coordinate value of (x, y)
	   *    boolean regression: "true" if you would like a regression line and formula created, false otherwise
	   *    boolean create_image: "true" if you would like to save graph as an image. Will prompt for name of file.
	   */
	   public static void createScatterChart(String title, String x_axis_label, String y_axis_label, String data_label, double[] x_values, double[] y_values, boolean regression, boolean create_image) {
	      XYSeriesCollection result = new XYSeriesCollection();
	      XYSeries series = new XYSeries(data_label);
	      for (int i = 0; i < x_values.length; i++) {
	         series.add(x_values[i], y_values[i]);
	   }
	      result.addSeries(series);
	      XYDataset data = result;

	      JFreeChart chart = ChartFactory.createScatterPlot(title, x_axis_label, y_axis_label, data, PlotOrientation.VERTICAL, true, true, false);
	        
	      if(regression) {
	         //get regression values
	        double regressionLine[] = Regression.getOLSRegression(data, 0);
	        String regressionFormula = "y = " + String.format("%.3f", regressionLine[1]) + "x + " + String.format("%.3f", regressionLine[0]);
	       
	        //Prepare a line function using the found parameters
	        LineFunction2D linefunction2d = new LineFunction2D(regressionLine[0], regressionLine[1]);
	 
	        //Creates a dataset by taking sample values from the line function
	        XYDataset dataset = DatasetUtilities.sampleFunction2D(linefunction2d, 0D, result.getDomainUpperBound(true), (int) result.getRangeUpperBound(true), regressionFormula);
	 
	        // Draw the line dataset
	        XYPlot xyplot = chart.getXYPlot();
	        xyplot.setDataset(1, dataset);
	        XYLineAndShapeRenderer xylineandshaperenderer = new XYLineAndShapeRenderer(true, false);
	        xylineandshaperenderer.setSeriesPaint(0, Color.BLUE);
	        xyplot.setRenderer(1, xylineandshaperenderer);
	      }

	      // create and display a frame...
	      ChartFrame frame = new ChartFrame("First", chart);
	      frame.pack();
	      frame.setVisible(true);
	        
	      if(create_image) {
	        Scanner scan = new Scanner(System.in);
	        System.out.println("Save scatter plot as (include .png): ");
	        String filename = scan.next();
	        scan.close();
	        System.out.println("Saved!");
	         try {
	            ChartUtilities.saveChartAsPNG(new File(filename), chart, 700, 450);
	         } catch (Exception e) {}
	      }
	}
	   
	     
	   /*
	   * Method: Create a Bar Chart
	   * Returns: Nothing, but graph will appear on screen
	   * Arguments:
	   *    String chartTitle: Title of chart
	   *    String x_axis_label: Label along x-axis
	   *    String y_axis_label: Label along y-axis
	   *    double[] values: All numerical values. Goes with y_axis_label.
	   *    String[] data_labels: All data labels. Sorted by color.
	   *    String[] category: All category labels. Goes with x_axis_label
	   *       EX: values[0] = 60, data_labels[0] = "January", category[0] = "safety"
	   *           values[1] = 15, data_labels[1] = "February", category[1] = "safety"
	   *           would result in a bar chart of heights 4 and 3, comparing various car types by safety ratings
	   *       Can include as many different data_labels and category values as needed as long as each string is equal.
	   *    boolean create_image: "true" if you would like to save graph as an image. Will prompt for name of file.
	   */
	   public static void createBarGraph(String chartTitle, String x_axis_label, String y_axis_label, double[] values, String[] data_labels, String[] category, boolean create_image) {
	      Sandbox sandbox = new Sandbox();
	      Sandbox.BarChart_AWT chart = sandbox.new BarChart_AWT(chartTitle, x_axis_label, y_axis_label, values, data_labels, category, create_image);
	      chart.pack( );       
	      chart.setVisible( true );
	   }
	   

	   /*
	   * Method: Create a Multi Line Chart
	   * Returns: Nothing, but graph will appear on screen
	   * Arguments:
	   *    String chartTitle: Title of chart
	   *    String x_axis_label: Label along x-axis
	   *    String y_axis_label: Label along y-axis
	   *    double[] values: All numerical values. Goes with y_axis_label.
	   *    String[] line_labels: All line labels. Sorted by color.
	   *    String[] category: All category labels. Goes with x_axis_label
	   *       EX: values[0] = 60, line_labels[0] = "First", category[0] = "January"
	   *           values[1] = 15, line_labels[1] = "Second", category[1] = "January"
	   *           values[2] = 48, line_labels[2] = "First", category[2] = "February"
	   *           values[3] = 30, line_labels[3] = "Second", category[3] = "February"
	   *           would result in a line chart containing the lines "First" and "Second" with charted values along x_axis_labels of "January" and "February"
	   *       Can include as many different values, data_labels, and category values as needed as long as each string is equal.
	   *    boolean create_image: "true" if you would like to save graph as an image. Will prompt for name of file.
	   */
	   public static void createMultiLineChart(String chartTitle, String x_axis_label, String y_axis_label, double[] values, String[] line_labels, String[] category, boolean create_image) {
	       Sandbox sandbox = new Sandbox();
	       Sandbox.LineChart_AWT chart = sandbox.new LineChart_AWT(chartTitle, x_axis_label, y_axis_label, values, line_labels, category, create_image);
	       chart.pack( );       
	       chart.setVisible( true );
	   }
	   

	   //Supporting class for graph creation. Do not edit.
	   public class BarChart_AWT extends JFrame {
	   
	      private static final long serialVersionUID = 1L;

	      public BarChart_AWT( String chartTitle, String x_axis_label, String y_axis_label, double[] values, String[] bar_labels, String[] category, boolean create_image) {
	         super(chartTitle);
	         final DefaultCategoryDataset dataset = new DefaultCategoryDataset( );  
	      
	         for(int i = 0; i < values.length; i++) {
	            dataset.addValue(values[i], bar_labels[i], category[i]);
	         }
	   
	         JFreeChart barChart = ChartFactory.createBarChart( chartTitle, x_axis_label, y_axis_label, dataset, PlotOrientation.VERTICAL, true, true, false);
	         
	         ChartPanel chartPanel = new ChartPanel( barChart );        
	         chartPanel.setPreferredSize(new java.awt.Dimension( 560 , 367 ) );        
	         setContentPane( chartPanel ); 
	   
	         if(create_image) {
	            Scanner scan = new Scanner(System.in);
	            System.out.println("Save bar chart as (include .png): ");
	            String filename = scan.next();
	            scan.close();
	            System.out.println("Saved!");
	            try {
	               ChartUtilities.saveChartAsPNG(new File(filename), barChart, 700, 450);
	            } catch (Exception e) {}
	         }
	  }
	   }
	   
	   //Supporting class for graph creation. Do not edit.
	   public class LineChart_AWT extends JFrame {
	   
	      public LineChart_AWT( String chartTitle, String x_axis_label, String y_axis_label, double[] values, String[] line_labels, String[] category, boolean create_image) {
	         super(chartTitle);
	         final DefaultCategoryDataset dataset = new DefaultCategoryDataset( );  
	      
	         for(int i = 0; i < values.length; i++) {
	            dataset.addValue(values[i], line_labels[i], category[i]);
	         }
	   
	         JFreeChart lineChart = ChartFactory.createLineChart( chartTitle, x_axis_label, y_axis_label, dataset, PlotOrientation.VERTICAL, true, true, false);
	         
	         //increase line thickness
	         ((lineChart.getCategoryPlot()).getRenderer()).setStroke(new java.awt.BasicStroke(2.5f)); 
	         
	         ChartPanel chartPanel = new ChartPanel( lineChart );        
	         chartPanel.setPreferredSize(new java.awt.Dimension( 560 , 367 ) );        
	         setContentPane( chartPanel ); 
	   
	         if(create_image) {
	            Scanner scan = new Scanner(System.in);
	            System.out.println("Save line chart as (include .png): ");
	            String filename = scan.next();
	            scan.close();
	            System.out.println("Saved!");
	            try {
	               ChartUtilities.saveChartAsPNG(new File(filename), lineChart, 700, 450);
	            } catch (Exception e) {}
	         }
	      }
	   }
	   
//-------------------------------------- Database and GUI Methods ----------------------------------------------	   
	 		/**
	 		 * Populates a JComboBox object with a specific column from the resulting query
	 		 * @param connectionString pass the connection string for the database
	 		 * @param SQL_command is the query you want to perform against the database
	 		 * @param box is the JComboBox object you wish to populate
	 		 * @param sql_column_to_use is the column of the result that you wish to use
	 		 */
	 		public static void populate_JComboBox(String connectionString, String SQL_command, JComboBox box, int sql_column_to_use)
	 		{ 		
	 			try
	 			{
	 				// create a connection to the database
	 				Connection conn = null;
	 		        conn = DriverManager.getConnection(connectionString);
	 		        
	 		        //create statement and execute
	 		        Statement stmt  = conn.createStatement();
	 		        ResultSet rs    = stmt.executeQuery(SQL_command);
	 		       
	 		       // loop through the result set and populate the comboBox

	 		        while (rs.next()) {
	 		        	box.addItem(rs.getString(sql_column_to_use));
	 		        }
	 		        
	 		       //close connections
	 		       rs.close();
	 		       conn.close();
	 			
	 		    } catch (SQLException e) {
	 				// TODO Auto-generated catch block
	 				e.printStackTrace();
	 				System.out.println("SQLite Database Error");
	 			} 	
	 		}
	 		
	 		/**
	 		 * utility method to display query results of SQLite databases into a jTable
	 		 *  @param connectionString pass the connection string for the database
	 		 * @param SQL_command  is the SQL query you want to run
	 		 * @param table is the JTable you want to populate
	 		 */
	 		public static void populate_JTable(String connectionString, String SQL_command, JTable table)
	 		{ 		
	 			try
	 			{
	 				// create a connection to the database
	 				Connection conn = null;
	 		        conn = DriverManager.getConnection(connectionString);
	 		        
	 		        //create statement and execute
	 		        Statement stmt  = conn.createStatement();
	 		        ResultSet rs    = stmt.executeQuery(SQL_command);
	 		       
	 		       // loop through the result set
	 		        ResultSetMetaData metaData = rs.getMetaData();

	 		        // names of columns
	 		        Vector<String> columnNames = new Vector<String>();
	 		        int columnCount = metaData.getColumnCount();
	 		        for (int column = 1; column <= columnCount; column++) {
	 		            columnNames.add(metaData.getColumnName(column));
	 		        }

	 		        // data of the table
	 		        Vector<Vector<Object>> data = new Vector<Vector<Object>>();
	 		        while (rs.next()) {
	 		            Vector<Object> vector = new Vector<Object>();
	 		            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
	 		                vector.add(rs.getObject(columnIndex));
	 		            }
	 		            data.add(vector);
	 		        }
	 		        
	 		        table.setModel(new DefaultTableModel(data, columnNames));
	 		       //close connections
	 		       rs.close();
	 		       conn.close();
	 			
	 		    } catch (SQLException e) {
	 				// TODO Auto-generated catch block
	 				e.printStackTrace();
	 				System.out.println("SQLite Database Error");
	 			} 	
	 		}	  
	 		
	 		/**
	 		 * 
	 		 * @param panel is the content pane object
	 		 * @param optional_file_filter is any extension you are looking for ("db", for example)
	 		 * @param optional_file_filter_description is the text you want to display in the filter
	 		 * @return provides a String of the database's path
	 		 */
	 		public static String select_file_and_return_path(JPanel panel, String optional_file_filter, String optional_file_filter_description) 
	 		{ 
	 			String path = "";
	 		    JFileChooser chooser = new JFileChooser();
	 		    if (optional_file_filter.length() > 0) //if the user supplied a file filter
	 		    {
	 		    	FileNameExtensionFilter filter = new FileNameExtensionFilter(optional_file_filter_description , optional_file_filter);
	 		    	chooser.setFileFilter(filter);
	 		    }
	 		    int returnVal = chooser.showOpenDialog(panel);
	 		    if(returnVal == JFileChooser.APPROVE_OPTION) {
	 		       System.out.println("You chose to open this file: " +
	 		            chooser.getSelectedFile().getName());
	 		       		path = chooser.getSelectedFile().getAbsolutePath();
	 		    }
	 		    
	 		    return path;
	 		}
	 		
	 		
	 		
	 		
	 		/**
	 		 * Allows the user to select a folder, and returns the path
	 		 * @param panel  is the contentPane
	 		 * @return a String of the path the user selected
	 		 */
	 		public static String select_folder_and_return_path(JPanel panel) 
	 		{ 
	 			String path = "";

	 			JFileChooser chooser = new JFileChooser();
	 			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	 			int returnVal = chooser.showOpenDialog(panel);
	 			if(returnVal == JFileChooser.APPROVE_OPTION) {
	 				System.out.println("You chose to open this file: " +
	 						chooser.getSelectedFile().getName());
	 				path = chooser.getSelectedFile().getAbsolutePath();
	 			}

	 			return path;
	 		}
	 		
//------------------------------------- Database Assistance --------------------------------------
	 		 
	 		/**utility method to pipe query results of SQLite databases into 2D array
	 		 * 
	 		 * @param database_location  is the file path to the database file itself
	 		 * @param SQL_command   is the query you want to run "select * from customers"
	 		 * @return a two-dimensional String array of the results table
	 		 */
	 		
	 		public static String[][] easy_sqlite_2d_array(String database_location, String SQL_command)
	 		{
	 			String [][] arrayToReturn;
	 			try
	 			{
	 				// create a connection to the database
	 				Connection conn = null;
	 		        conn = DriverManager.getConnection(database_location);
	 		        
	 		        //create statement and execute
	 		        Statement stmt  = conn.createStatement();
	 		        ResultSet rs    = stmt.executeQuery(SQL_command);
	 		       
	 		       // loop through the result set
	 		        ResultSetMetaData metaData = rs.getMetaData();


	 		        int columnCount = metaData.getColumnCount();


	 		        //get how many records in dataset 
	 		        //because we need to know beforehand how large the array needs to be
	 		        int rowCount = 0;
	 		        while (rs.next()) {
	 		        	//weird thing we have to do with SQLite because does not support record-set ordering
	 		        	rowCount++;
	 		        }
	 		        rs.close();
	 		     
	 		        //create array
	 		        arrayToReturn = new String[columnCount][rowCount];
	 		        
	 		        
	 		        // data of the table //redo query and populate array
	 		        rs = stmt.executeQuery(SQL_command);
	 		        int row_counter = 0;
	 		        while (rs.next()) {

	 		            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
	 		                arrayToReturn[columnIndex-1][row_counter] =  rs.getObject(columnIndex).toString();
	 		            }
	 		            row_counter ++;

	 		        }
	 		        
	 		       //close connections
	 		       rs.close();
	 		       conn.close();
	 		       return arrayToReturn;
	 			
	 		    } catch (SQLException e) {
	 				// TODO Auto-generated catch block
	 				e.printStackTrace();
	 				System.out.println("SQLite Database Error");
	 				
	 				//return nothing
	 				arrayToReturn = new String[0][0];
	 				return arrayToReturn;
	 				
	 			} 	
	 		}
	 		
	 		/**utility method to pipe query results of SQLite databases into 2D array
	 		 * 
	 		 * @param connection_string  is the file path to the database file itself
	 		 * @param SQL_command   is the query you want to run "select * from customers"
	 		 * @param data_column_to_return  is the column to return as array
	 		 * @return a  String array of the results in the selected column
	 		 */
	 		public static String[] easy_sqlite_1D_array(String connection_string, String SQL_command, int data_column_to_return)
	 		{ 
	 			try
	 			{         
	 				// create a connection to the database
	 				Connection conn = null;
	 				conn = DriverManager.getConnection(connection_string);

	 				//create statement and execute
	 				Statement stmt  = conn.createStatement();
	 				ResultSet rs    = stmt.executeQuery(SQL_command);

	 				ArrayList<String> data_to_return = new ArrayList<String>();

	 				// loop through the result set
	 				while (rs.next())
	 				{
	 					//System.out.println(rs.getInt("customerid") +  "\t" + rs.getString("lastname"));
	 					data_to_return.add(rs.getString(data_column_to_return).toUpperCase());
	 				}
	 				//close connections
	 				rs.close();
	 				conn.close();
	 				
	 				//modify the Arraylist back to an array
	 				String[] converted = data_to_return.toArray(new String[data_to_return.size()]);
	 				return converted;
	 				
	 			} catch (SQLException e) {
	 				// TODO Auto-generated catch block
	 				e.printStackTrace();
	 				System.out.println("SQLite Database Error");
	 				return null;
	 			}
	 		}
	 		
	 		
	 		/**utility method to allow for UPDATE and INSERT commands to SQLite databases
	 		 * 
	 		 * @param database_location  is the file path to the database file itself
	 		 * @param SQL_command   is the query you want to run "select * from customers"
	 		 * @return true if it ran without error (note, this doesn't mean a row was affected!)
	 		 */	 		
	 		public static boolean easy_sqlite_modify(String database_location, String SQL_command)
	 		{
	 			try
	 			{	        
	 				System.out.println(SQL_command);
	 				// create a connection to the database
	 				Connection conn = null;
	 		        conn = DriverManager.getConnection(database_location);
	 		        
	 		        //create statement and execute
	 		        Statement stmt  = conn.createStatement();
	 		        stmt.execute(SQL_command);
	 		  
	 		        conn.close();
	 		        return true;
	 			
	 		    } catch (SQLException e) {
	 				// TODO Auto-generated catch block
	 				e.printStackTrace();
	 				System.out.println("SQLite Database Error");
	 				return false;
	 			} 	
	 		}
	 		
	 		
	 
}
