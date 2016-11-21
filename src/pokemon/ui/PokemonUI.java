package pokemon.ui;

import java.lang.reflect.Field;
import java.text.Collator;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

import org.eclipse.swt.SWT;
//import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;



import pokemon.data.Pokemon;
import pokemon.data.Trainer;
import pokemon.data.Type;

/**
 * Pokemon UIDialog displays Pokemons in SWT Table Widget
 *
 */
public class PokemonUI extends Dialog {

	private List<Pokemon> pokemons = new ArrayList<Pokemon>();
	private Set<Trainer> trainers = new HashSet<Trainer>();

	/**
	 * @param parent
	 * @param pokemons
	 */
	public PokemonUI(Shell parent, List<Pokemon> pokemons) {
		// Pass the default styles here
		this(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL, pokemons);
	}

	/**
	 * @param parent
	 * @param style
	 * @param pokemons
	 */
	public PokemonUI(Shell parent, int style, List<Pokemon> pokemons) {
		// Let users override the default styles
		super(parent, style);
		setText("Pokemon Manager");
		setPokemons(pokemons);
		//CAUTION setTrainers depends from List<Pokemon> pokemons
		setTrainers();
	}

	/**
	 * Opens the dialog
	 */
	public void open() {
		// Create the dialog window
		Shell shell = new Shell(getParent(), getStyle());
		shell.setText(getText());
		createContents(shell);
		shell.pack();
		shell.open();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	public List<Pokemon> getPokemons() {
		return pokemons;
	}

	public void setPokemons(List<Pokemon> pokemons) {
		this.pokemons = pokemons;
	}
	
	public Set<Trainer> getTrainers() {
	        return trainers;
	}
	
	public void setTrainers() {
	    for (Pokemon p : pokemons) {
	        trainers.add(p.getTrainer());
	    }
	}
	

	/**
	 * Creates the dialog's contents
	 * 
	 * @param shell
	 *            the dialog window
	 */
	private void createContents(final Shell shell) {

		shell.setLayout(new GridLayout());
		Table table = new Table(shell, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);
		//perform general setup of the Table 
		table.setLinesVisible(true);
		table.setHeaderVisible(true);		
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.heightHint = 105;
		data.widthHint = 800;
		table.setLayoutData(data);
		//create table headers using TableColumn 
		List<String> heads = getTableHeaders();
		for (String head : heads) {
		    TableColumn column = new TableColumn(table, SWT.NONE);
		    column.setText(head);
		    column.pack();
		}
		//create table rows using TableItem, each row of the table is one Pokemon  
                int i = 0;
                for (Pokemon p : getPokemons()) {
                    TableItem item = new TableItem(table, SWT.None);
                    item.setText(i++, String.valueOf(p.getNumber()));
                    item.setText(i++, p.getName());
                    item.setText(i++, String.valueOf(p.getType()));
                    item.setText(i++, String.valueOf(p.getTrainer()));
                    item.setText(i++, String.valueOf(p.getSwaps().size()));
                    item.setText(i++,String.valueOf(p.isSwapAllow()));
                    item.setText(i++, String.valueOf(p.getCompetitions().size()));
                    i = 0;                  
                }
                
		
		//implement sorting using addListener(SWT.Selection, new Listener() {... 
                // sorting
                for (TableColumn column : table.getColumns()) {
                    //create a generic sort listener for each column which sorts
                    // columns descend order
                    column.setData("SortOrder",0);
                    column.addListener(SWT.Selection, new Listener() {
                        public void handleEvent(Event event) {
                            // determine the column index
                            int index = 0;
                            if (event.widget instanceof TableColumn) {
                                index = table.indexOf((TableColumn) event.widget);
                            }
                            TableItem[] items = table.getItems();
                            Collator collator = Collator.getInstance(Locale.getDefault());
                            // fetch the actual sort order for the column
                            int sortOrder = 0;
                            try {
                                sortOrder = Integer.valueOf(column.getData("SortOrder").toString());
                            } catch (Exception e) {
                                sortOrder = 0;
                            }
                            for (int i = 0; i < items.length; i++) {
                                String value1 = items[i].getText(index);
                                for (int j = 0; j < i; j++) {
                                    String value2 = items[j].getText(index);
                                    // sort in descend order
                                    if (sortOrder == 0) {
                                        if (collator.compare(value1, value2) < 0) {
                                            List<String> values = new ArrayList<String>();
                                            for (int k = 0; k < heads.size(); k++) {
                                                values.add(items[i].getText(k));
                                            }
                                            items[i].dispose();
                                            TableItem item = new TableItem(table, SWT.NONE, j);
                                            item.setText(values.toArray(new String[values.size()]));
                                            items = table.getItems();
                                            break;
                                        }
                                    }
                                    // sort ascend order
                                    if (sortOrder == 1) {
                                        if (collator.compare(value1, value2) > 0) {
                                            List<String> values = new ArrayList<String>();
                                            for (int k = 0; k < heads.size(); k++) {
                                                values.add(items[i].getText(k));
                                            }
                                            items[i].dispose();
                                            TableItem item = new TableItem(table, SWT.NONE, j);
                                            item.setText(values.toArray(new String[values.size()]));
                                            items = table.getItems();
                                            break;
                                        }
                                    }
                                }
                            }
                            // change the actual sort order to the opposite value
                            if (sortOrder == 0) {
                                column.setData("SortOrder", 1);
                            } else {
                                column.setData("SortOrder", 0);
                            }
                        }
                    });
                }
                
                
                
                // editor for the text table cell
                final TableEditor editor = new TableEditor(table);
                editor.horizontalAlignment = SWT.LEFT;
                editor.grabHorizontal = true;
                table.addListener(SWT.MouseDoubleClick, new Listener() {
                        public void handleEvent(Event event) {
                                Rectangle clientArea = table.getClientArea();
                                Point pt = new Point(event.x, event.y);
                                int index = table.getTopIndex();
                                // iterate through the tables row
                                while (index < table.getItemCount()) {
                                        boolean visible = false;
                                        final TableItem item = table.getItem(index);
                                        // the tables columns index creating the text editor is the
                                        // 2nd column (=1) of table
                                        int columnIndex = 1;
                                        Rectangle rect = item.getBounds(columnIndex);
                                        if (rect.contains(pt)) {
                                                // create a text input box
                                                final Text text = new Text(table, SWT.NONE);
                                                // create a listener for the text input box
                                                Listener textListener = new Listener() {
                                                        public void handleEvent(final Event e) {
                                                                switch (e.type) {
                                                                case SWT.FocusOut:
                                                                        // update the items text with the text from
                                                                        // the input
                                                                        item.setText(columnIndex, text.getText());
                                                                        
                                                                        if (pokemons.get(table.getSelectionIndices()[0]).getNumber() == Integer.valueOf(item.getText(0)))  {
                                                                        	System.out.println(pokemons.get(table.getSelectionIndices()[0]).toString() + " " + "name changed to " + text.getText());
                                                                        	pokemons.get(table.getSelectionIndices()[0]).setName(text.getText());
                                                                        	System.out.println(pokemons.get(table.getSelectionIndices()[0]).toString());
                                                                        }
                                                                        
                                                                        
                                                                        
                                                                        // and update attached data element if it exist
                                                                        if (item.getData() != null) {
                                                                                if (item.getData() instanceof Pokemon)
                                                                                ((Pokemon)item.getData()).setName(text.getText());
                                                                        }
                                                                        text.dispose();
                                                                        break;
                                                                case SWT.Traverse:
                                                                        switch (e.detail) {
                                                                        case SWT.TRAVERSE_RETURN:
                                                                                item.setText(columnIndex, text.getText());
                                                                                
                                                                                if (pokemons.get(table.getSelectionIndices()[0]).getNumber() == Integer.valueOf(item.getText(0)))  {
                                                                                	System.out.println(pokemons.get(table.getSelectionIndices()[0]).toString() + " " + "name changed to " + text.getText());
                                                                                	pokemons.get(table.getSelectionIndices()[0]).setName(text.getText());
                                                                                	System.out.println(pokemons.get(table.getSelectionIndices()[0]).toString());
                                                                                }
                                                                                
                                                                                // FALL THROUGH
                                                                        case SWT.TRAVERSE_ESCAPE:
                                                                                text.dispose();
                                                                                e.doit = false;
                                                                        }
                                                                        break;
                                                                }
                                                        }
                                                };
                                                // add the listener to the text input box for
                                                // deselection
                                                text.addListener(SWT.FocusOut, textListener);
                                                // and selection
                                                text.addListener(SWT.Traverse, textListener);
                                                editor.setEditor(text, item, columnIndex);
                                                text.setText(item.getText(columnIndex));
                                                text.selectAll();
                                                text.setFocus();
                                                return;
                                        }
                                        if (!visible && rect.intersects(clientArea)) {
                                                visible = true;
                                        }
                                        if (!visible)
                                                return;
                                        index++;
                                }
                                //while
                        }
                });
                              
                
                // editor for the change of the Type
                table.addListener(SWT.MouseDoubleClick, new Listener() {               
                    public void handleEvent(Event event) {
                    	
                        //MenuType to choose the Type
                        Menu menuType = new Menu(shell);        
                        menuType.setVisible(false);
                        table.setMenu(menuType);    
                        
                        MenuItem createType1 = new MenuItem(menuType, SWT.None);
                        MenuItem createType2 = new MenuItem(menuType, SWT.None);
                        MenuItem createType3 = new MenuItem(menuType, SWT.None);
                        createType1.setText("Fire");
                        createType2.setText("Water");
                        createType3.setText("Poison");   
                                          
                            Rectangle clientArea = table.getClientArea();
                            Point pt = new Point(event.x, event.y);
                            int index = table.getTopIndex();
                            // iterate through the tables row
                            while (index < table.getItemCount()) {
                                    boolean visible = false;
                                    final TableItem item = table.getItem(index);
                                    // the tables columns index creating the text editor is the
                                    // 2nd column (=1) of table
                                    int columnIndex = 2;
                                    Rectangle rect = item.getBounds(columnIndex);
                                    if (rect.contains(pt)) {                      

                                        menuType.setVisible(true);
                                        // Listener for the action performed when menu item is selected
                                        createType1.addSelectionListener(new SelectionAdapter() {
                                            @Override
                                            public void widgetSelected(SelectionEvent e) {
                                                Object o = e.getSource();
                                                if (o != null) {
                                                    if (o instanceof MenuItem) {
                                                        System.out.println(o.getClass().getSimpleName() + " '"
                                                                + ((MenuItem) o).getText() + "' has been selected");
                                                    }
                                                }
                                                 
                                                // TODO: Implementierung falls Type auf Fire gesetzt wird
                                                item.setText(columnIndex, "Fire");
                                                if (pokemons.get(table.getSelectionIndices()[0]).getNumber() == Integer.valueOf(item.getText(0)))  {
                                                	System.out.println(pokemons.get(table.getSelectionIndices()[0]).toString() + " " + "type changed to Fire");
                                                	pokemons.get(table.getSelectionIndices()[0]).setType(Type.Fire);
                                                	System.out.println(pokemons.get(table.getSelectionIndices()[0]).toString());
                                                }
                                                shell.pack();
                                            }
                                        });
                                        
                                        createType2.addSelectionListener(new SelectionAdapter() {
                                            @Override
                                            public void widgetSelected(SelectionEvent e) {
                                                Object o = e.getSource();
                                                if (o != null) {
                                                    if (o instanceof MenuItem) {
                                                        System.out.println(o.getClass().getSimpleName() + " '"
                                                                + ((MenuItem) o).getText() + "' has been selected");
                                                    }
                                                }
                                                    
                                                // TODO: Implementierung falls Type auf Water gesetzt wird
                                                item.setText(columnIndex, "Water");
                                                if (pokemons.get(table.getSelectionIndices()[0]).getNumber() == Integer.valueOf(item.getText(0)))  {
                                                	System.out.println(pokemons.get(table.getSelectionIndices()[0]).toString() + " " + "type changed to Water");
                                                	pokemons.get(table.getSelectionIndices()[0]).setType(Type.Water);
                                                	System.out.println(pokemons.get(table.getSelectionIndices()[0]).toString());
                                                }
                                                shell.pack();
                                            }
                                        });
                                        
                                        createType3.addSelectionListener(new SelectionAdapter() {
                                            @Override
                                            public void widgetSelected(SelectionEvent e) {
                                                Object o = e.getSource();
                                                if (o != null) {
                                                    if (o instanceof MenuItem) {
                                                        System.out.println(o.getClass().getSimpleName() + " '"
                                                                + ((MenuItem) o).getText() + "' has been selected");
                                                    }
                                                }
                                                    
                                                // TODO: Implementierung falls Type auf Poisson gesetzt wird
                                                item.setText(2, "Poison");
                                                if (pokemons.get(table.getSelectionIndices()[0]).getNumber() == Integer.valueOf(item.getText(0)))  {
                                                	System.out.println(pokemons.get(table.getSelectionIndices()[0]).toString() + " " + "type changed to Poisson");
                                                	pokemons.get(table.getSelectionIndices()[0]).setType(Type.Poison);
                                                	System.out.println(pokemons.get(table.getSelectionIndices()[0]).toString());
                                                }
                                                shell.pack();
                                            }
                                        });
                                        return;
                                    }
                                    if (!visible && rect.intersects(clientArea)) {
                                            visible = true;
                                    }
                                    if (!visible) {
                                            menuType.setVisible(false);
                                            return;
                                    }
                                    index++;
                            }
                    }
            });
                
                
                //Menu to choose the swapAllowed 
                table.addListener(SWT.MouseDoubleClick, new Listener() {               
                    public void handleEvent(Event event) {
                    	
                        //MenuType to choose the Type
                        Menu menuSwapAllow = new Menu(shell);        
                        menuSwapAllow.setVisible(false);
                        table.setMenu(menuSwapAllow);    
                        MenuItem createTrue = new MenuItem(menuSwapAllow, SWT.None);
                        MenuItem createFalse = new MenuItem(menuSwapAllow, SWT.None);
                        createTrue.setText("True");
                        createFalse.setText("False");

                            Rectangle clientArea = table.getClientArea();
                            Point pt = new Point(event.x, event.y);
                            int index = table.getTopIndex();
                            // iterate through the tables row
                            while (index < table.getItemCount()) {
                                    boolean visible = false;
                                    final TableItem item = table.getItem(index);
                                    // the tables columns index creating the text editor is the
                                    // 2nd column (=1) of table
                                    int columnIndex = 5;
                                    Rectangle rect = item.getBounds(columnIndex);
                                    if (rect.contains(pt)) {                      

                                    	menuSwapAllow.setVisible(true);
                                        // Listener for the action performed when menu item is selected
                                    	createTrue.addSelectionListener(new SelectionAdapter() {
                                            @Override
                                            public void widgetSelected(SelectionEvent e) {
                                                Object o = e.getSource();
                                                if (o != null) {
                                                    if (o instanceof MenuItem) {
                                                        System.out.println(o.getClass().getSimpleName() + " '"
                                                                + ((MenuItem) o).getText() + "' has been selected");
                                                    }
                                                }

                                                // TODO: Implementierung falls SwapAllowed auf true gesetzt wird
                                                item.setText(columnIndex, "true");
                                                if (pokemons.get(table.getSelectionIndices()[0]).getNumber() == Integer.valueOf(item.getText(0)))  {
                                                	System.out.println(pokemons.get(table.getSelectionIndices()[0]).toString() + " " + "SwapAllowed changed to true");
                                                	pokemons.get(table.getSelectionIndices()[0]).setSwapAllow(true);
                                                	System.out.println(pokemons.get(table.getSelectionIndices()[0]).toString());
                                                }
                                                shell.pack();
                                            }
                                        });
                                        
                                    	createFalse.addSelectionListener(new SelectionAdapter() {
                                            @Override
                                            public void widgetSelected(SelectionEvent e) {
                                                Object o = e.getSource();
                                                if (o != null) {
                                                    if (o instanceof MenuItem) {
                                                        System.out.println(o.getClass().getSimpleName() + " '"
                                                                + ((MenuItem) o).getText() + "' has been selected");
                                                    }
                                                }
                                                // TODO: Implementierung falls SwapAllowed auf false gesetzt wird.
                                                item.setText(columnIndex, "false");
                                                if (pokemons.get(table.getSelectionIndices()[0]).getNumber() == Integer.valueOf(item.getText(0)))  {
                                                	System.out.println(pokemons.get(table.getSelectionIndices()[0]).toString() + " " + "SwapAllowed changed to false");
                                                	pokemons.get(table.getSelectionIndices()[0]).setSwapAllow(false);
                                                	System.out.println(pokemons.get(table.getSelectionIndices()[0]).toString());
                                                }
                                                shell.pack();
                                            }
                                        });
                                        return;
                                    }
                                    if (!visible && rect.intersects(clientArea)) {
                                            visible = true;
                                    }
                                    if (!visible) {
                                    	menuSwapAllow.setVisible(false);
                                            return;
                                    }
                                    index++;
                            }
                    }  
            });              
                
                // create a context menu
                Menu contextMenu = new Menu(shell);
                contextMenu.setVisible(false);
                table.setMenu(contextMenu);
                MenuItem miCreate = new MenuItem(contextMenu, SWT.None);
                miCreate.setText("Create Pokemon");
                // Listener for the action performed when menu item is selected
                miCreate.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        Object o = e.getSource();
                        if (o != null) {
                            if (o instanceof MenuItem) {
                                System.out.println(o.getClass().getSimpleName() + " '"
                                        + ((MenuItem) o).getText() + "' has been selected");
                            }
                        }
                        Pokemon p = new Pokemon("", Type.Fire);
                        //Choose a random Trainer
                        int size = trainers.size();
                        int number = new Random().nextInt(size); // In real life, the Random object should be rather more shared than this
                        int i = 0;
                        for(Trainer obj : trainers)
                        {
                            if (i == number)
                                p.setTrainer(obj);
                            i = i + 1;
                        }
                        //set swapAllow false
                        p.setSwapAllow(false);
                        pokemons.add(p);
                        // TODO: Create a new TableItem with the Pokemon data
                        TableItem item = new TableItem(table, SWT.NONE);               
                        // attach the Pokemon instance to the item
                        item.setData(p);
                        //item.setText(0, p.getType().name());
                        item.setText(0, String.valueOf(p.getNumber()));
                        item.setText(1, p.getName());
                        item.setText(2, String.valueOf(p.getType()));
                        item.setText(3, String.valueOf(p.getTrainer()));
                        item.setText(4, String.valueOf(p.getSwaps().size()));
                        item.setText(5,String.valueOf(p.isSwapAllow()));
                        item.setText(6, String.valueOf(p.getCompetitions().size()));
                        //createCComboEditorTrainer(table, item, 3, trainers);
                    }
                });
                MenuItem miCreate2 = new MenuItem(contextMenu, SWT.None);
                miCreate2.setText("Delete Pokemon");
                // Listener for the action performed when menu item is selected
                miCreate2.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        Object o = e.getSource();
                        if (o != null) {
                            if (o instanceof MenuItem) {
                                System.out.println(o.getClass().getSimpleName() + " '"
                                        + ((MenuItem) o).getText() + "' has been selected");
                            }
                        }
                        //CAUTION: ungeprüftes löschen!!!
                        // -Besser wäre es zu prüfen, ob der Index des Pokemons in der GUI mit dem Index
                        // des Pokemons in der Pokemonliste pokemons übereinstimmt.
                        int help = table.getSelectionIndices()[0];
                        pokemons.remove(help);
                        table.remove(table.getSelectionIndices());
                        shell.pack();
                    }
                });
                                            
                // Listener to show the Menu when a right click is performed in the
                // table
                table.addListener(SWT.MouseDown, new Listener() {
                    @Override
                    public void handleEvent(Event event) {
                        //TableItem[] selection = table.getSelection();
                        if ( (event.button == 3)) {
                            //menuType.setVisible(false);
                            contextMenu.setVisible(true);
                        }
                    }
                });
                
                //Lister welcher alle klickevents wahrnimmt
                SelectionAdapter klickevent2 = new SelectionAdapter(){
               	 @Override
               	 public void widgetSelected(SelectionEvent e) {
                      int indexPokemon2 = table.getSelectionIndices()[0];
                      System.out.println("Klickevent2");
                      System.out.println(indexPokemon2);
                	 } 
               };                
               
                
                //Lister welcher alle klickevents wahrnimmt
                SelectionAdapter klickevent = new SelectionAdapter(){
               	 @Override
               	 public void widgetSelected(SelectionEvent e) {
                      int indexPokemon2 = table.getSelectionIndices()[0];
                      System.out.println("Klickevent2 deaktiviert");
                      System.out.println(indexPokemon2);
                      table.removeSelectionListener(klickevent2);
               		 }
               	 
               	 public int selectedItem() {
               		 int indexPokemon2 = table.getSelectionIndices()[0];
               		 return indexPokemon2;
               	 }
               };
               
               

                
                table.addSelectionListener(klickevent);
                

                //MenuItem for SWAP
                MenuItem miCreate3 = new MenuItem(contextMenu, SWT.None);
                miCreate3.setText("Swap Pokemon");
                // Listener for the action performed when menu item is selected
                miCreate3.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        Object o = e.getSource();
                        if (o != null) {
                            if (o instanceof MenuItem) {
                                System.out.println(o.getClass().getSimpleName() + " '"
                                        + ((MenuItem) o).getText() + "' has been selected");
                            }
                        }
                        
                     //Save the Pokemon which was selected for swap
                     int indexPokemon1 = table.getSelectionIndices()[0];
                     System.out.println(indexPokemon1);
                     table.addSelectionListener(klickevent2);
                     System.out.println("klickevent2 aktiv");
                     

                    } 
                });   
           	 
                
                
          
	}

	/**
	 * Create table headers String
	 * 
	 * @return
	 */
	private List<String> getTableHeaders() {
		List<String> ret = new ArrayList<String>();
	        // TODO: Create the headers for the Table based on Pokemon attributes  
		for (Field f : Pokemon.class.getDeclaredFields()) {
	            if (!java.lang.reflect.Modifier.isStatic(f.getModifiers())) {
	                ret.add(f.getName().substring(0, 1).toUpperCase() + f.getName().substring(1, f.getName().length()) + "         " );
	            }
	        }
		return ret;
	}
	
	
// ##############################################################ENDE####################################################################	
	
//	/**
//	 * Creates a CCombo with an attached Editor for a TableItem in the specified
//	 * columnIndex with selected Type as default
//	 *
//	 * @param table
//	 *            the Table to create the CCombo in
//	 * @param item
//	 *            the TableItem (row) to create the CCombo in
//	 * @param columnIndex
//	 *            the column index to create the CCombo in
//	 * @param selected
//	 *            the Type selected as default
//	 */
//	private static void createCComboEditor(Table table, TableItem item, int columnIndex, Type selected) {
//	    // editor for the drop down element
//	    final TableEditor ceditor = new TableEditor(table);
//	    // create a Drop Dowon box based on the contents of a enum
//	    CCombo combo = new CCombo(table, SWT.NONE);
//	    combo.addSelectionListener(new SelectionAdapter() {
//	        @Override
//	        public void widgetSelected(SelectionEvent e) {
//	            System.out.println(e);
//	            // get the selected Type
//	            Type t = Type.valueOf(combo.getText());
//	            // set the selected type to the pokemon instance attached
//	            if (item.getData() != null) {
//	                if(item.getData() instanceof Pokemon){
//	                    ((Pokemon) item.getData()).setType(t);
//	                    // print changed pokemon
//	                    System.out.println(item.getData());   
//	                }
//	            }
//	        }
//	    });
//	    String tname = "";
//	    for (Type t : Type.values()) {
//	        tname = t.name();
//	        combo.add(tname);
//	    }
//	    if (selected == null) {
//	        // set the last element as selected
//	        combo.setText(tname);
//	    } else {
//	        combo.setText(selected.name());
//	    }
//	    ceditor.grabHorizontal = true;
//	    // place the combo box in the actual row in the 1st column (=0) of
//	    // table
//	    ceditor.setEditor(combo, item, columnIndex);
//	}
//	
//        /**
//         * Creates a CCombo with an attached Editor for a TableItem in the specified
//         * columnIndex with selected Type as default
//         *
//         * @param table
//         *            the Table to create the CCombo in
//         * @param item
//         *            the TableItem (row) to create the CCombo in
//         * @param columnIndex
//         *            the column index to create the CCombo in
//         * @param selected
//         *            the Type selected as default
//         */
//        private static void createCComboEditorTrainer(Table table, TableItem item, int columnIndex, Set<Trainer> ttrainers) {
//            // editor for the drop down element
//            final TableEditor ceditor = new TableEditor(table);
//            // create a Drop Down box based on the contents of a enum
//            CCombo combo = new CCombo(table, SWT.NONE);
//            combo.addSelectionListener(new SelectionAdapter() {
//                @Override
//                public void widgetSelected(SelectionEvent e) {
//                    System.out.println(e);
//                    // get the selected Type
//                    //Type t = Type.valueOf(combo.getText());
//                    String [] helpString2 = combo.getText().split(" ");
//                    Trainer helpTrainer= new Trainer(helpString2[0],helpString2[1]);
//                    // set the selected type to the pokemon instance attached
//                    if (item.getData() != null) {
//                        if(item.getData() instanceof Pokemon){
//                            //((Pokemon) item.getData()).setType(t);
//                            ((Pokemon) item.getData()).setTrainer(helpTrainer);                            
//                            // print changed pokemon
//                            System.out.println(item.getData());
//                        }
//                    }
//                }
//                
//            });
//            String trainerName = "";
//            for (Trainer t : ttrainers) {
//                trainerName = t.getFirstname();
//                trainerName += " ";
//                trainerName += t.getLastname();
//                combo.add(trainerName);
//            }
//            ceditor.grabHorizontal = true;
//            // place the combo box in the actual row in the 1st column (=0) of
//            // table
//            ceditor.setEditor(combo, item, columnIndex);
//        }
	
//  //Zum editieren von Spalte 5
//  table.addListener(SWT.MouseDoubleClick, new Listener() {
//      public void handleEvent(Event event) {
//              Rectangle clientArea = table.getClientArea();
//              Point pt = new Point(event.x, event.y);
//              int index = table.getTopIndex();
//              // iterate through the tables row
//              while (index < table.getItemCount()) {
//                      boolean visible = false;
//                      final TableItem item = table.getItem(index);
//                      // the tables columns index creating the text editor is the
//                      // 2nd column (=1) of table
//                      int columnIndex = 5;
//                      Rectangle rect = item.getBounds(columnIndex);
//                      if (rect.contains(pt)) {
//                              // create a text input box
//                              final Text text = new Text(table, SWT.NONE);
//                              // create a listener for the text input box
//                              Listener textListener = new Listener() {
//                                      public void handleEvent(final Event e) {
//                                              switch (e.type) {
//                                              case SWT.FocusOut:
//                                                      // update the items text with the text from
//                                                      // the input
//                                                      item.setText(columnIndex, text.getText());
//                                                      // and update attached data element if it exist
//                                                      if (item.getData() != null) {
//                                                              if (item.getData() instanceof Pokemon)
//                                                              ((Pokemon)item.getData()).setName(text.getText());
//                                                      }
//                                                      text.dispose();
//                                                      break;
//                                              case SWT.Traverse:
//                                                      switch (e.detail) {
//                                                      case SWT.TRAVERSE_RETURN:
//                                                              item.setText(columnIndex, text.getText());
//                                                              // FALL THROUGH
//                                                      case SWT.TRAVERSE_ESCAPE:
//                                                              text.dispose();
//                                                              e.doit = false;
//                                                      }
//                                                      break;
//                                              }
//                                      }
//                              };
//                              // add the listener to the text input box for
//                              // deselection
//                              text.addListener(SWT.FocusOut, textListener);
//                              // and selection
//                              text.addListener(SWT.Traverse, textListener);
//                              editor.setEditor(text, item, columnIndex);
//                              text.setText(item.getText(columnIndex));
//                              text.selectAll();
//                              text.setFocus();
//                              return;
//                      }
//                      if (!visible && rect.intersects(clientArea)) {
//                              visible = true;
//                      }
//                      if (!visible)
//                              return;
//                      index++;
//              }
//              //while
//      }
//});
	
	
}