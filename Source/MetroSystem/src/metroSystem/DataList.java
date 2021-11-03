package metroSystem;

import java.util.ArrayList;

public abstract class DataList {
    //    private volatile static DataList uniqueInstance;
    protected ArrayList<NodeEntry>[] data;
    protected Integer size;

    public DataList() {
        Database db = Database.getInstance();
        size = db.getStationCount();
        data = new ArrayList[size+1];
        for (int i = 0; i < size+1; i++) {
        	data[i] = new ArrayList<NodeEntry>();
        }
        createGraph();
    }

//    /**
//     * Apply double-checked locking method to create this important singleton object.
//     * @return  the unique DataList instance
//     * @since   Oct. 3, 2021
//     */
//    public static DataList getInstance() {
//        if(uniqueInstance == null) {
//            synchronized (DataList.class) {
//                if(uniqueInstance == null) {
//                    uniqueInstance = new DataList();
//                }
//            }
//        }
//        return uniqueInstance;
//    }

    /**
     * Load the data from Database and Create the data graph.
     */
    protected abstract void createGraph();

    /**
     * Retrieve back all the neighbors of the station {@code index}.
     * @return  An array of ArrayList holds all the adjacency nodes information
     */
    public ArrayList<NodeEntry>[] getGraph(){
        return data;
    }

    /**
     * Retrieve back all the neighbors of the station {@code index}.
     * @param index The index number of the target station
     * @return  A ArrayList holds all its neighbors information
     */
    public ArrayList<NodeEntry> getNeighbors(int index){
        return data[index];
    }
    
    /**
     * Retrieve back the size of the stations.
     * @return  A Integer.
     */
    public Integer getSize() {
    	return size;
    }
    
    /**
     * Print out the Data_List as an Adjacency_List for debug use only.
     */
    public void debugPrint(){
        for (int i = 0; i < data.length; i++ ) {
            System.out.printf("Station %d >>>\t", i);
            ArrayList<NodeEntry> array = data[i];
            for (NodeEntry node : array) {
                System.out.print("<" + node.getKey() + " (" + node.getValue() + ")> - ");
            }
            System.out.println();
        }
    }
}
