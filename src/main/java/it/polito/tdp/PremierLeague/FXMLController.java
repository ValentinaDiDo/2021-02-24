
/**
 * Sample Skeleton for 'Scene.fxml' Controller Class
 */

package it.polito.tdp.PremierLeague;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;

import it.polito.tdp.PremierLeague.model.Match;
import it.polito.tdp.PremierLeague.model.Model;
import it.polito.tdp.PremierLeague.model.Player;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FXMLController {

	private Model model;
	Graph<Player, DefaultWeightedEdge> grafo;
	private boolean grafoCreato = false;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="btnCreaGrafo"
    private Button btnCreaGrafo; // Value injected by FXMLLoader

    @FXML // fx:id="btnGiocatoreMigliore"
    private Button btnGiocatoreMigliore; // Value injected by FXMLLoader

    @FXML // fx:id="btnSimula"
    private Button btnSimula; // Value injected by FXMLLoader

    @FXML // fx:id="cmbMatch"
    private ComboBox<Match> cmbMatch; // Value injected by FXMLLoader

    @FXML // fx:id="txtN"
    private TextField txtN; // Value injected by FXMLLoader

    @FXML // fx:id="txtResult"
    private TextArea txtResult; // Value injected by FXMLLoader

    @FXML
    void doCreaGrafo(ActionEvent event) {
    	Match match = this.cmbMatch.getValue();
    	if(match.equals(null)) {
    		txtResult.setText("per favore scegli una partita");
    	}else {
    		this.model.creaGrafo(match);
    		
    		this.grafoCreato = true;
    		this.grafo = this.model.getGrafo();
    		
    		txtResult.appendText("GRAFO CREATO\n");
    		txtResult.appendText("\nVERTICI: "+this.grafo.vertexSet().size());
    		txtResult.appendText("\nARCHI: "+this.grafo.edgeSet().size());
    	}
    }

    @FXML
    void doGiocatoreMigliore(ActionEvent event) {    	
    	if(this.grafoCreato == false) {
    		txtResult.setText("DEVI PRIMA CREARE IL GRAFO\n");
    	}else {
    		Player migliore = this.model.calcolaGiocatoreMigliore();
    		double grado = this.model.calcolaGrado(migliore);
    		
    		txtResult.setText("GIOCATORE MIGLIORE : "+migliore.getName()+" | ∆ : "+grado);
    	}
    }
    
    @FXML
    void doSimula(ActionEvent event) {
    	if(this.grafoCreato == false) {
    		txtResult.setText("DEVI PRIMA CREARE IL GRAFO\n");
    	}else {
    		String n = txtN.getText();
    		if(n.equals("")) {
    			txtResult.appendText("\nRIEMPI IL CAMPO\n");;
    		}else {
    			try {
    				int N = Integer.parseInt(n);
    				this.model.Simula(N);
    			}catch(NumberFormatException e ) {
    				e.printStackTrace();
    				txtResult.appendText("\nPER FAVORE INSERISCI SOLO NUMERI\n");
    			}
    		}
    	}
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert btnCreaGrafo != null : "fx:id=\"btnCreaGrafo\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnGiocatoreMigliore != null : "fx:id=\"btnGiocatoreMigliore\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnSimula != null : "fx:id=\"btnSimula\" was not injected: check your FXML file 'Scene.fxml'.";
        assert cmbMatch != null : "fx:id=\"cmbMatch\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtN != null : "fx:id=\"txtN\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Scene.fxml'.";

    }
    
    public void setModel(Model model) {
    	this.model = model;
    	
    	this.cmbMatch.getItems().clear();
    	List<Match> match = this.model.getAllMatches();
    	Collections.sort(match);
    	this.cmbMatch.getItems().addAll(match);
    }
}
