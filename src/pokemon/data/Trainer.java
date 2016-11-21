package pokemon.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author paul
 *
 */
public class Trainer implements Serializable {
	
	/**	 */
	private static final long serialVersionUID = -2957959275032785669L;
	/** */
	private String firstname;
	/** */
	private String lastname;
	// for ex 1.2 : Pokemon pokemon; + getter and setter methods  
	/** */
	private List<Pokemon> pokemons = new ArrayList<Pokemon>();

	/**
	 * @param firstname
	 * @param lastname
	 */
	public Trainer(String firstname, String lastname) {
		this.firstname = firstname;
		this.lastname = lastname;
	}

	/**
	 * @return
	 */
	public String getFirstname() {
		return firstname;
	}

	/**
	 * @param firstname
	 */
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	/**
	 * @return
	 */
	public String getLastname() {
		return lastname;
	}

	/**
	 * @param lastname
	 */
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	/**
	 * @return
	 */
	public List<Pokemon> getPokemons() {
		return pokemons;
	}

 
	/**
	 * @param pokemons
	 * 
	 * for bidirectional linking it is necessary to set this as trainer
	 */
	public void setPokemons(List<Pokemon> pokemons) {
		this.pokemons = pokemons;
		for (Pokemon p : getPokemons()) {
			p.setTrainer(this); // set this as trainer for all
		}
	}
	
	/**
	 * @param index
	 * @return
	 */
	public Pokemon getPokemon(int index) {
		return pokemons.get(index);
	}

	/**
	 * @param pokemon
	 */
	public void addPokemon(Pokemon pokemon) {
		getPokemons().add(pokemon); // add to list 
		pokemon.setTrainer(this); // set as trainer
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return getFirstname() +" " + getLastname();
	}
	
}
