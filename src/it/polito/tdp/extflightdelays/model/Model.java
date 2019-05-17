package it.polito.tdp.extflightdelays.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graphs;
import org.jgrapht.event.ConnectedComponentTraversalEvent;
import org.jgrapht.event.EdgeTraversalEvent;
import org.jgrapht.event.TraversalListener;
import org.jgrapht.event.VertexTraversalEvent;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;

import it.polito.tdp.extflightdelays.db.ExtFlightDelaysDAO;


public class Model {
	
	SimpleWeightedGraph<Airport,DefaultWeightedEdge> grafo;
	//idMap dove salvo aeroporti , la passo al metodo del dao per popolarla
	Map<Integer,Airport> aIdMap;
	
	//mappa visita di aiporti,(relazione padre/figlio
	Map<Airport, Airport> visita;
	ExtFlightDelaysDAO dao;
	
	public Model() {
	
		this.grafo =  new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		this.aIdMap = new HashMap<Integer,Airport>();
		this.visita=new HashMap<Airport,Airport>();
		dao= new ExtFlightDelaysDAO();
		dao.loadAllAirports(aIdMap);

	}

	public Collection<Airport> getAirports(){
		return this.aIdMap.values();
	}

	public void creaGrafo(int distanzaMedia) {
		
		//aggiungo i vertici
		
		Graphs.addAllVertices(grafo, aIdMap.values());
		
		for(Rotta rotta: dao.getRotte(aIdMap, distanzaMedia)) {
			// controllo se esiste gia un arco tra i 2
			// se esiste aggiorno il peso

			DefaultWeightedEdge edge = grafo.getEdge(rotta.getSource(), rotta.getDestination());
			if (edge == null) {
				//se non esiste arco edge lo aggiungo
				Graphs.addEdge(grafo, rotta.getSource(), rotta.getDestination(), rotta.getMediaDistanza());
				//altrimenti mi recupero il peso che avevo e lo aggiorno
			} else {		
				//System.out.println("aggiornare il peso!");
				double peso = grafo.getEdgeWeight(edge);
				double newPeso = (peso + rotta.getMediaDistanza()) / 2;
				System.out.println("Aggiornare il peso, peso vecchio :"+peso+ "peso nuovo: "+newPeso);
				grafo.setEdgeWeight(edge, newPeso);
				
			}
			
		}
		System.out.println("Grafo creato");
		System.out.println("vertici: " +grafo.vertexSet().size());
		System.out.println("archi: " + grafo.edgeSet().size());
	}
	
	public boolean testConnessione (Integer a1, Integer a2) {
		
		//metodo che riceve i due id aeroporti , verifico se li ho visitati
		Set<Airport> visitati= new HashSet<Airport>();		
		Airport partenza = aIdMap.get(a1);
		Airport destinazione= aIdMap.get(a2);
		System.out.println("Testo connessione tra " +partenza + " e" +destinazione);
		
		//visita in AMPIEZZA a partire da un nodo
		BreadthFirstIterator<Airport, DefaultWeightedEdge> it= new BreadthFirstIterator<>(this.grafo, partenza);
		//finchè itero aggiungo alla lista 
		while(it.hasNext()) {
			visitati.add(it.next());
		}
		//controllo se nel set c'è la destinazione
		if(visitati.contains(destinazione))
			return true;
		else
			return false;
	}
	
	
	public List <Airport> trovaPercorso(Integer a1, Integer a2){
		// metodo che restituisce percorso della visita e mi salvo in una lista gli aeroporti che incontro 
		
		List<Airport> percorso= new ArrayList<Airport>();
		Airport partenza = aIdMap.get(a1);
		Airport destinazione= aIdMap.get(a2);
		
		System.out.println("Cerco percorso tra " + partenza + " e " + destinazione);
		
		//faccio la visita: iteratore e addTraversaListener
		BreadthFirstIterator<Airport, DefaultWeightedEdge> it= new BreadthFirstIterator<>(this.grafo, partenza);
		//setto la partenza 
		visita.put(partenza, null);
		
		it.addTraversalListener(new TraversalListener<Airport, DefaultWeightedEdge>() {
			//implemento interfaccia con questi metodi
			@Override
			public void vertexTraversed(VertexTraversalEvent<Airport> arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void vertexFinished(VertexTraversalEvent<Airport> arg0) {
				// TODO Auto-generated method stub
				
			}
			//a me interessa questo che salva arco attraversato 
			@Override
			public void edgeTraversed(EdgeTraversalEvent<DefaultWeightedEdge> ev) {
				
				Airport sorgente= grafo.getEdgeSource(ev.getEdge());
				Airport destinazione= grafo.getEdgeTarget(ev.getEdge());
				
				//la mappa nn contiene destinazione ma contiene sorgente
				if(!visita.containsKey(destinazione) && visita.containsKey(sorgente)) {
					visita.put(destinazione, sorgente);
					
				//la mappa nn contiene sorg ma contiene dest
				}else if (!visita.containsKey(sorgente) && visita.containsKey(destinazione)){
						visita.put(sorgente, destinazione);
					}
				}
			
			
			@Override
			public void connectedComponentStarted(ConnectedComponentTraversalEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void connectedComponentFinished(ConnectedComponentTraversalEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		//faccio la visita
		while(it.hasNext())
			it.next();
		
		//2 aeroporti non collegati
		if( !visita.containsKey(partenza) || !visita.containsKey(destinazione)) {
			return null;
			
			}
		 
		
		
		Airport step=destinazione;
		while(!step.equals(partenza)) {
			percorso.add(step);
			step=visita.get(step);
		}
		//aggiunge ultimo nodo
		percorso.add(step);
		return percorso;
		}

	public boolean isValid(String distanzaInput) {
		
		return distanzaInput.matches("\\d+");
	}

}
