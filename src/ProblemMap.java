import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ProblemMap extends Problem {
	Map<String, Map<String, Double>> map;
	Map<String, Double> sld;

	public Object goalState;

	public ProblemMap(String mapfilename) throws Exception {
		map = new HashMap<String, Map<String, Double>>();
		//read map from file of source-destination-cost triples (tab separated)
		BufferedReader reader = new BufferedReader(new FileReader(mapfilename));
		String line;
		while ((line = reader.readLine()) != null) {
			String[] strA = line.split("\t");

			String from_city = strA[0], to_city = strA[1];
			Double cost = Double.parseDouble(strA[2]);

			if (!map.containsKey(from_city))
				map.put(from_city, new HashMap<String, Double>());
			map.get(from_city).put(to_city, cost);

			//putting the reverse edge as well
			if (!map.containsKey(to_city))
				map.put(to_city, new HashMap<String, Double>());
			map.get(to_city).put(from_city, cost);
		}
		reader.close();
	}

	public ProblemMap(String mapfilename, String heuristicfilename) throws Exception {
		this(mapfilename);

		sld = new HashMap<String, Double>();
		BufferedReader reader = new BufferedReader(new FileReader(heuristicfilename));
		String line;
		while ((line = reader.readLine()) != null) {
			String[] strA = line.split("\t");

			String city = strA[0];
			double h = Double.parseDouble(strA[1]);

			sld.put(city, h);
		}
		reader.close();
	}

	boolean goal_test(Object state) {
		return state.equals(goalState);
	}

	Set<Object> getSuccessors(Object state) {
		Set<Object> result = new HashSet<Object>();
		for (Object successor_state : map.get(state).keySet())
			result.add(successor_state);
		return result;
	}

	double step_cost(Object fromState, Object toState) {
		return map.get(fromState).get(toState);
	}

	public double h(Object state) {
		return sld.get(state);
	}

	public static void main(String[] args) throws Exception {
		ProblemMap problem = new ProblemMap("romania.txt", "romaniaSLD.txt");
		problem.initialState = "Timisoara";
		problem.goalState = "Bucharest";

		Search search = new Search(problem);

		// search methods: BFS, DFS

		System.out.println("BreadthFirstTreeSearch:\t\t" + search.BreadthFirstTreeSearch());

		System.out.println("BreadthFirstGraphSearch:\t" + search.BreadthFirstGraphSearch());

		System.out.println("DepthFirstTreeSearch:\t\t" + search.DepthFirstTreeSearch());

		System.out.println("DepthFirstGrapthSearch:\t\t" + search.DepthFirstGraphSearch());

		System.out.println("UniformCostGraphSearch:\t\t" + search.UniformCostGraphSearch());

		System.out.println("UniformCostTreeSearch:\t\t" + search.UniformCostTreeSearch());

		System.out.println("IterativeDeepeningTreeSearch:\t" + search.IterativeDeepeningTreeSearch());

		System.out.println("IterativeDeepeningGraphSearch:\t" + search.IterativeDeepeningGraphSearch());

		System.out.println("AStarTreeSearch:\t\t\t" + search.AstarTreeSearch());
		printTree(search.node_list);
		System.out.println("AStarGraphSearch:\t\t\t" + search.AstarGraphSearch());
		printTree(search.node_list);

	}

	public static void printTree(List<Node> node_list){
		sortTree(node_list);
		for(int i=0; i<node_list.size(); i++){
			Node curr = node_list.get(i);
			for(int d=0; d<curr.depth; d++)
				System.out.print("  ");
			System.out.println(curr.state + "(g=" + curr.path_cost + ", h=" + sld.get(curr.state) +
					", f="+ (curr.path_cost+ sld.get(curr.state)) + ")" + " order=" + curr.order);

		}
	}

	public static void sortTree(List<Node> node_list){
		for(int i=0; i<node_list.size(); i++){
			Node parent = node_list.get(i);
			for(int j=i+1; j<node_list.size();j++){
				Node curr = node_list.get(j);
				if(curr.parent_node == parent){
					node_list.remove(j);
					node_list.add(i+1, curr);
				}
			}
		}
	}
}
