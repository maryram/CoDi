package handlers;

import it.unimi.dsi.fastutil.ints.*;

import java.io.*;
import java.util.*;

import beans.*;

public class LinksHandler {

    private Set<Edge> links;
    private int[] vertexArray;
    private Int2IntOpenHashMap vertexId2Index;
    private HashMap<Integer, IntArrayList> outlinks, inlinks;
    private int nVertices, nDirectedEdges,nUndirectedEdges;
    
    private int lines, duplicates;

    public void read(String fileName) throws IOException {
        System.out.println("------------------------------------------------");
        System.out.println("Reading links from: " + fileName);
        TreeSet<Integer> us = new TreeSet<Integer>();
        outlinks = new HashMap<Integer, IntArrayList>();
        inlinks = new HashMap<Integer, IntArrayList>();
        BufferedReader br = new BufferedReader(new FileReader(
                new File(fileName)));
        String line = br.readLine();
       //begin maryam
        while(!(line.equals(""))){
        //System.out.println("Header: " + line);
        line = br.readLine();
        }
        //end maryam
        line = br.readLine();
        int user1, user2;
        StringTokenizer st;
        IntArrayList friends, followers;
        while (line != null) {
            if (!line.startsWith("#")) {
                st = new StringTokenizer(line,",");
                user1 = Integer.parseInt(st.nextToken());
                user2 = Integer.parseInt(st.nextToken());
                us.add(user1);
                us.add(user2);
            }
            line = br.readLine();
        }
        nVertices = us.size();
        vertexArray = toInt(us);
        vertexId2Index = new Int2IntOpenHashMap(nVertices);
        for (int u = 0; u < vertexArray.length; u++) {
            vertexId2Index.add(vertexArray[u], u);
        }
        links = new HashSet<Edge>();
        br = new BufferedReader(new FileReader(new File(fileName)));
        line=br.readLine();
      //begin maryam
        while(!(line.equals(""))){
        //System.out.println("Header: " + line);
        line = br.readLine();
        }
        //end maryam
        line = br.readLine();
        Edge e;
        while (line != null) {
            // PARSE LINE
            st = new StringTokenizer(line,",");
            user1 = Integer.parseInt(st.nextToken());
            user2 = Integer.parseInt(st.nextToken());
            e = new Edge(user1, user2);
       
            boolean insert = links.add(e);
            if (insert) {
                friends = outlinks.get(user1);
                if (friends == null) {
                    friends = new IntArrayList();
                    outlinks.put(user1, friends);
                }
                if (!friends.contains(user2)) {
                    friends.add(user2);
                }
                followers = inlinks.get(user2);
                if (followers == null) {
                    followers = new IntArrayList();
                    inlinks.put(user2, followers);
                }
                if (!followers.contains(user1))
                    followers.add(user1);
            } else {
                duplicates++;
            }
            lines++;
            line = br.readLine();
        }
        br.close();
        nDirectedEdges = links.size();
    
        nUndirectedEdges=0;
        for(Edge e1:links){
        	if(e1.source<e1.destination)
        		nUndirectedEdges++;
        	else
        		if(!links.contains(new Edge(e1.destination,e1.source)))
        			nUndirectedEdges++;
        }
        
        
        System.out.println("Links loaded!");
        System.out.println("Number of duplicates\t"+duplicates);
        System.out.println("------------------------------------------------");
    }

    
    public int countBidirectionalLinks(){
    	int count=0;
    	for(Edge e:links){
    		if(e.source<e.destination && links.contains(new Edge(e.destination,e.source)))
    			count++;
    	}
    	return count;
    }
    
    public void printInfo() {
        int maxOutLink = Integer.MIN_VALUE, minOutLink = Integer.MAX_VALUE;
        int maxFollowers = Integer.MIN_VALUE, minFollowers = Integer.MAX_VALUE;
        double avgOutLink = 0.0;
        double avgFollowers = 0.0;
        
        for (int userId : vertexArray) {
            int nFriends = getOutLinksForVertex(userId).size();
            if (nFriends > maxOutLink)
                maxOutLink = nFriends;
            if (nFriends < minOutLink)
                minOutLink = nFriends;
            avgOutLink += nFriends;
            int nFollowers = getInlinksForVertex(userId).size();
            if (nFollowers > maxFollowers)
                maxFollowers = nFollowers;
            if (nFollowers < minFollowers)
                minFollowers = nFollowers;
            avgFollowers += nFollowers;
        }
        avgOutLink /= nVertices;
        avgFollowers /= nVertices;
        System.out.println("------------------------------------------------");
        System.out.println(". . . . . . . . . Links info . . . . . . . . . .");

        System.out.println("#vertices\t" + nVertices);
        System.out.println("#Directed edges\t" + nDirectedEdges);
        System.out.println("#Undirected edges\t" + nUndirectedEdges);
        System.out.println("Bidirectional links\t"+countBidirectionalLinks());
        System.out.println("Avg # outlinks \t" + avgOutLink);
        System.out.println("Min # outlinks \t" + minOutLink);
        System.out.println("Max # outlinks \t" + maxOutLink);
        System.out.println("Avg # inlinks \t" + avgFollowers);
        System.out.println("Min # inlinks \t" + minFollowers);
        System.out.println("Max # inlinks \t" + maxFollowers);
        System.out.println("------------------------------------------------");
        
        
        
    }


    
    
    
    public void maryam_printInfo(FileWriter Result) throws IOException {
        int maxOutLink = Integer.MIN_VALUE, minOutLink = Integer.MAX_VALUE;
        int maxFollowers = Integer.MIN_VALUE, minFollowers = Integer.MAX_VALUE;
        double avgOutLink = 0.0;
        double avgFollowers = 0.0;
        
        for (int userId : vertexArray) {
            int nFriends = getOutLinksForVertex(userId).size();
            if (nFriends > maxOutLink)
                maxOutLink = nFriends;
            if (nFriends < minOutLink)
                minOutLink = nFriends;
            avgOutLink += nFriends;
            int nFollowers = getInlinksForVertex(userId).size();
            if (nFollowers > maxFollowers)
                maxFollowers = nFollowers;
            if (nFollowers < minFollowers)
                minFollowers = nFollowers;
            avgFollowers += nFollowers;
        }
        avgOutLink /= nVertices;
        avgFollowers /= nVertices;
      //  System.out.println("------------------------------------------------");
        Result.write("Links info:\t#vertices\t#Directed edges\t#Undirected edges\tBidirectional links\tAvg#outlinks \tMin#outlinks \tMax#outlinks \tAvg#inlinks \tMin#inlinks \tMax#inlinks \n");
        Result.write("           \t");
        Result.write(nVertices+"\t");
        Result.write(nDirectedEdges+"\t");
        Result.write(nUndirectedEdges+"\t");
        Result.write(countBidirectionalLinks()+"\t");
        Result.write(avgOutLink+"\t");
        Result.write(minOutLink+"\t");
        Result.write(maxOutLink+"\t");
        Result.write(avgFollowers+"\t");
        Result.write(minFollowers+"\t");
        Result.write(maxFollowers+"\n");
      //  System.out.println("------------------------------------------------");
        
        
        
    }

    
    
    
    
    
    
    
    
    
    
    
    
    
    public int[] toInt(Set<Integer> set) {
        int[] a = new int[set.size()];
        int i = 0;
        for (Integer val : set)
            a[i++] = val;
        return a;
    }

    public boolean existsEdge(int userId1, int userId2) {
        return links.contains(new Edge(userId1, userId2));
    }

    public IntArrayList getOutLinksForVertex(int nodeId) {
        IntArrayList ris = outlinks.get(nodeId);
        if (ris != null)
            return ris;
        else
            return new IntArrayList();
    }

    public IntArrayList getInlinksForVertex(int vertexId) {
        IntArrayList ris = inlinks.get(vertexId);
        if (ris != null)
            return ris;
        else
            return new IntArrayList();
    }

    public int getNDirectedEdges() {
        return nDirectedEdges;
    }

    public int getNUnDirectedEdges() {
        return nUndirectedEdges;
    }

    public int[] getVertexArray() {
        return vertexArray;
    }

    public int getNVertices() {
        return nVertices;
    }

    public Int2IntOpenHashMap getVertexId2Index() {
        return vertexId2Index;
    }

    public Set<Edge> getEdges() {
        return links;
    }
    
    
    public Set<Edge> getUndirectedEdges(){
    	HashSet<Edge> undirectedEdges=new HashSet<Edge>();
    	
    	for(Edge e: links){
    		if(!undirectedEdges.contains(e) && !undirectedEdges.contains(new Edge(e.destination,e.source)) ){
    			undirectedEdges.add(e);
    		}	
    	}
    	return undirectedEdges;
    }
    
    
    public static void main(String[] args) throws IOException {
    	String linkPath=args[0];
    	LinksHandler lh=new LinksHandler();
    	lh.read(linkPath);
    	lh.printInfo();
	}

	public void addEdges(HashSet<Edge> sigmaEdges) {
        IntArrayList friends, followers;

		for(Edge e:sigmaEdges){
			e = new Edge(e.source, e.destination);
            boolean insert = links.add(e);
            if (insert) {
                friends = outlinks.get(e.source);
                if (friends == null) {
                    friends = new IntArrayList();
                    outlinks.put(e.source, friends);
                }
                if (!friends.contains(e.destination)) {
                    friends.add(e.destination);
                }
                followers = inlinks.get(e.destination);
                if (followers == null) {
                    followers = new IntArrayList();
                    inlinks.put(e.destination, followers);
                }
                if (!followers.contains(e.source))
                    followers.add(e.source);
            }
		}
	}//addEdges
	
}