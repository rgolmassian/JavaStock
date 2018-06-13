import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.awt.event.ActionEvent;

public class StockApp extends JFrame {

	private JPanel contentPane;
	private JTable table;
	public String db_location;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					StockApp frame = new StockApp();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public StockApp() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton btnOpenDatabase = new JButton("Open Database");
		
	
		btnOpenDatabase.setBounds(6, 27, 153, 29);
		contentPane.add(btnOpenDatabase);
		
		JLabel lblStock = new JLabel("Stock:");
		lblStock.setBounds(171, 32, 61, 16);
		contentPane.add(lblStock);
		
		JComboBox comboBox = new JComboBox();
		
		comboBox.setBounds(224, 28, 209, 27);
		contentPane.add(comboBox);
		
		JButton btnUpdatedSelected = new JButton("Update Selected");
		
		btnUpdatedSelected.setBounds(6, 76, 153, 29);
		contentPane.add(btnUpdatedSelected);
		
		JButton btnUpdateAll = new JButton("Update All");
		
		btnUpdateAll.setBounds(224, 76, 117, 29);
		contentPane.add(btnUpdateAll);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(16, 117, 417, 155);
		contentPane.add(scrollPane);
		
		table = new JTable();
		scrollPane.setViewportView(table);
		
		btnOpenDatabase.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				db_location = Sandbox.select_file_and_return_path(contentPane, "db", "SQLite Files");
				String sqlquery = "Select stock from stocks";
				Sandbox.populate_JComboBox("jdbc:sqlite:" + db_location, sqlquery, comboBox, 1);
				
			}
		});
		
		comboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				String selection = comboBox.getSelectedItem().toString();
				String sqlquery = "Select * from stock_price where stock = '" + selection + "'";
				Sandbox.populate_JTable("jdbc:sqlite:" + db_location, sqlquery, table);
				
			}
		});
		
		btnUpdatedSelected.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				String selection = comboBox.getSelectedItem().toString();
				String wheretosave = Sandbox.select_folder_and_return_path(contentPane);
				
				
				Sandbox.download_URL("http://money.cnn.com/quote/quote.html?symb=" + selection, wheretosave + "/download.txt");
				
				String file = Sandbox.read_file_into_string(wheretosave + "/download.txt");
				
				String price = "streamFormat\\s*=\\s*\\\"ToHundredth\\\"\\s*streamFeed\\s*=\\s*\\\"MorningstarQuote\\\"\\>(.*?)\\<";
				String prev = "\\<td\\>Previous\\s*close\\s*\\<\\/td\\>\\<td\\s*class=\\\"wsod_quoteData Point\\\"\\>(.*?)\\<";
				String open = "\\<td\\>Today\\&rsquo\\;s\\s*open\\<\\/td\\>\\<td\\s*class=\\\"wsod_quot eDataPoint\\\"\\>(.*?)\\<";
				
				System.out.println("Where: "+wheretosave);
				
				String[] priceData = Sandbox.easy_regex(price, file, false);
				String[] prevData = Sandbox.easy_regex(prev, file, false);
				String[] openData = Sandbox.easy_regex(open, file, false);
				
				System.out.println("PriceData: "+Arrays.asList(priceData));
				System.out.println("PrevData: "+Arrays.asList(prevData));
				System.out.println("OpenData: "+Arrays.asList(openData));
				
				
				priceData = new String[] {"150"};
				prevData = new String[] {"205"};
				openData = new String[] {"350"};
				
				System.out.println(priceData[0] + ", " + prevData[0] + "," + openData[0]);
				
				String sqlupdate = "Insert into stock_price (stock, previousclose, currentopen, currentprice, datetime) " + 
										"values ('" + selection + "', " + priceData[0] + "," + prevData[0] + "," + openData[0] + ", +  strftime('%s', 'now'))";
		
				System.out.println(Sandbox.easy_sqlite_modify("jdbc:sqlite:" + db_location, sqlupdate));
		
				
				
			}
		});
		
		
		btnUpdateAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				String sqlquery = "Select stock from stocks";
				String[] allstocks = Sandbox.easy_sqlite_1D_array("jdbc:sqlite:" + db_location, sqlquery, 1);
				
				String wheretosave = Sandbox.select_folder_and_return_path(contentPane);
				
				for (int item = 0; item < allstocks.length; item++)
				{
					String selection = allstocks[item];
					
					
					Sandbox.download_URL("http://money.cnn.com/quote/quote.html?symb=" + selection, wheretosave + "/download.txt");
					
					String file = Sandbox.read_file_into_string(wheretosave + "/download.txt");
					
					String price = "streamFormat=\\\"ToHundredth\\\" streamFeed=\\\"BatsUS\\\">(.*?)";
					String prev = "Previous close<\\/td><td class=\\\"wsod_quoteDataPoint\\\">(.*?)<\\/td>";
					String open = "<td>Today\\&rsquo\\;s open<\\/td><td class=\"wsod_quoteDataPoint\">(.*?)<\\/td>";
					
					System.out.println("Where: "+wheretosave);
					
					String[] priceData = Sandbox.easy_regex(price, file, false);
					String[] prevData = Sandbox.easy_regex(prev, file, false);
					String[] openData = Sandbox.easy_regex(open, file, false);
					
					System.out.println("PriceData: "+Arrays.asList(priceData));
					System.out.println("PrevData: "+Arrays.asList(prevData));
					System.out.println("OpenData: "+Arrays.asList(openData));
					
					priceData = new String[] {"150"};
					//prevData = new String[] {"205"};
					//openData = new String[] {"350"};
					
					System.out.println(priceData[0] + ", " + prevData[0] + "," + openData[0]);
					
					String sqlupdate = "Insert into stock_price (stock, previousclose, currentopen, currentprice, datetime) " + 
											"values ('" + selection + "', " + priceData[0] + "," + prevData[0] + "," + openData[0] + ", +  strftime('%s', 'now'))";
			
					System.out.println(Sandbox.easy_sqlite_modify("jdbc:sqlite:" + db_location, sqlupdate));
					
				}
				
				
			}
		});
		

	}
}
