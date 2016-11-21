package pokemon;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import pokemon.data.Pokemon;
import pokemon.data.Trainer;
import pokemon.data.Type;
import pokemon.ui.PokemonUI;

/**
 * The PokemonManager Class
 */
public class PokemonManager implements Serializable {
	
	/**	 */
	private static final long serialVersionUID = -8341409284810316647L;

	/***/
	private static final String STORAGE_PATH = "pokemonsManager";
	
	private static List<Pokemon> pokemons = new ArrayList<Pokemon>();

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		// load and create some Pokemons if no pokemons could be loaded
		pokemons = loadPokemons();
		if (pokemons.isEmpty()) {
			pokemons = createPokemons();
		}

		// loaded Pokemons
		System.out.println("Pokemons which are loaded or created");
		for (Pokemon p : pokemons) {
			System.out.println(p);
		}
		
		// create a SWT window
		Display display = new Display();
		Shell shell = new Shell(display);
		PokemonUI pui = new PokemonUI(shell, pokemons);
		pui.open();
		
		// stored Pokemons
		System.out.println("Pokemons which are going to be stored");		
		for (Pokemon p : pokemons){
			System.out.println(p.toString());
		}
		
		// store
	    storePokemons(pokemons);
	}
	
	/**
	 * Stores the list of Pokemons
	 * 
	 * @param ps List<Pokemons> with Pokemon instances to be stored
	 */
	public static void storePokemons(List<Pokemon> ps) {
		try {
			System.out.println("Storing " + ps.size() + " pokemons");
			// use ObjectOutputStream to write Objects
			// use FileOutputStream to write to a File 
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(STORAGE_PATH));
			oos.writeObject(ps);
			oos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Loads a list of Pokemons
	 * 
	 * @return List<Pokemons>
	 */
	@SuppressWarnings("unchecked")
	public static List<Pokemon> loadPokemons() {
		List<Pokemon> ps = new ArrayList<Pokemon>();
		try {
			// use ObjectInputStream to read Objects
			// use FileInputStream to read from a File 
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(STORAGE_PATH));
			ps = (List<Pokemon>) ois.readObject();
			ois.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		System.out.println("Loaded " + ps.size() + " pokemons");
		return ps;
	}
	
	
	
	
	private static List<Pokemon> createPokemons(){
	    Pokemon p0 = new Pokemon("Pikachu", Type.Poison);
	    p0.addSwap(new Swap());
	    p0.addSwap(new Swap());
	    p0.addSwap(new Swap());
	    p0.addSwap(new Swap());
	    p0.addCompetition(new Competition());
	    p0.addCompetition(new Competition());
	    Pokemon p1 = new Pokemon("Carapuce", Type.Water);
	    p1.setSwapAllow(false);
	    p1.addCompetition(new Competition());
	    p1.addCompetition(new Competition());
	    p1.addCompetition(new Competition());
	    p1.addCompetition(new Competition());
	    p1.addCompetition(new Competition());
	    p1.addCompetition(new Competition());
	    p1.addCompetition(new Competition());
	    Pokemon p2 = new Pokemon("Raupy", Type.Fire);
	    p2.addSwap(new Swap());
	    p2.addSwap(new Swap());
	    p2.addSwap(new Swap());
	    p2.addCompetition(new Competition());
	    Trainer t0 = new Trainer("Peter", "Lustig");
	    t0.addPokemon(p0);
	    Trainer t1 = new Trainer("Alisa", "Traurig");
	    t1.addPokemon(p1);
	    t1.addPokemon(p2);
	    pokemons.add(p0);
	    pokemons.add(p1);
	    pokemons.add(p2);
	    return pokemons;	    
	}
}
