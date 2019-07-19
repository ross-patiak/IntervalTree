package structures;

import java.util.ArrayList;

/**
 * Encapsulates an interval tree.
 * 
 * @author runb-cs112
 */
public class IntervalTree {
	
	/**
	 * The root of the interval tree
	 */
	IntervalTreeNode root;
	
	/**
	 * Constructs entire interval tree from set of input intervals. Constructing the tree
	 * means building the interval tree structure and mapping the intervals to the nodes.
	 * 
	 * @param intervals Array list of intervals for which the tree is constructed
	 */
	public IntervalTree(ArrayList<Interval> intervals) {
		
		
		// make a copy of intervals to use for right sorting
		ArrayList<Interval> intervalsRight = new ArrayList<Interval>(intervals.size());
		for (Interval iv : intervals) {
			intervalsRight.add(iv);
		}
		
		// rename input intervals for left sorting
		ArrayList<Interval> intervalsLeft = intervals;
		
		// sort intervals on left and right end points
		sortIntervals(intervalsLeft, 'l');
		sortIntervals(intervalsRight,'r');
		
		// get sorted list of end points without duplicates
		ArrayList<Integer> sortedEndPoints = 
							getSortedEndPoints(intervalsLeft, intervalsRight);
		
		// build the tree nodes
		root = buildTreeNodes(sortedEndPoints);
		
		// map intervals to the tree nodes
		mapIntervalsToTree(intervalsLeft, intervalsRight);
	}
	
	/**
	 * Returns the root of this interval tree.
	 * 
	 * @return Root of interval tree.
	 */
	public IntervalTreeNode getRoot() {
		return root;
	}
	
	/**
	 * Sorts a set of intervals in place, according to left or right endpoints.  
	 * At the end of the method, the parameter array list is a sorted list. 
	 * 
	 * @param intervals Array list of intervals to be sorted.
	 * @param lr If 'l', then sort is on left endpoints; if 'r', sort is on right endpoints
	 */
	public static void sortIntervals(ArrayList<Interval> intervals, char lr) {
		
		
		if(lr == 'l') {
			for(int i = 1; i < intervals.size(); i++) {
				Interval tmp = intervals.get(i);
				
				int j = i - 1;
				while(j >= 0 && tmp.leftEndPoint < intervals.get(j).leftEndPoint) {
					intervals.set(j + 1, intervals.get(j));
					j--;
				}
				
				intervals.set(j + 1, tmp);
			
			
			}
		}
		
		if(lr == 'r') {
			for(int i = 1; i < intervals.size(); i++) {
				Interval tmp = intervals.get(i);
				
				int j = i - 1;
				while(j >= 0 && tmp.rightEndPoint < intervals.get(j).rightEndPoint) {
					intervals.set(j + 1, intervals.get(j));
					j--;
				}
				
				intervals.set(j + 1, tmp);
			
			
			}
		}
		
	}	
	
	/**
	 * Given a set of intervals (left sorted and right sorted), extracts the left and right end points,
	 * and returns a sorted list of the combined end points without duplicates.
	 * 
	 * @param leftSortedIntervals Array list of intervals sorted according to left endpoints
	 * @param rightSortedIntervals Array list of intervals sorted according to right endpoints
	 * @return Sorted array list of all endpoints without duplicates
	 */
	public static ArrayList<Integer> getSortedEndPoints(ArrayList<Interval> leftSortedIntervals, ArrayList<Interval> rightSortedIntervals) {
				
		ArrayList<Integer> points = new ArrayList<Integer>();
		
		if(leftSortedIntervals == null) {
			leftSortedIntervals = new ArrayList<Interval>();
		}
		
		if(rightSortedIntervals == null) {
			rightSortedIntervals = new ArrayList<Interval>();
		}
		
		points.add(leftSortedIntervals.get(0).leftEndPoint);
		
		for(int i = 1; i < leftSortedIntervals.size(); i++) {				//MAYBE NULLPOINTER
			
			if(points.indexOf(leftSortedIntervals.get(i).leftEndPoint) == -1) {
				points.add(leftSortedIntervals.get(i).leftEndPoint);
			}
			
		}	//end of left iv loop
		
		for(int i = 0; i < rightSortedIntervals.size(); i++) {
			
			int temp = rightSortedIntervals.get(i).rightEndPoint;
			
			if(points.indexOf(temp) == -1) {			//MAYBE LOSING DATA
				
					for(int j = 0; i < points.size(); j++) {
						
						if(temp > points.get(points.size() -1)) {
							points.add(temp);
							break;
						}
						
						if(points.get(j) > temp) {
							points.add(j - 1, temp);
							break;
						} 
					
					}
			}
			
			
		}	//end of right iv loop
		
		return points;
		
	}
	
	/**
	 * Builds the interval tree structure given a sorted array list of end points
	 * without duplicates.
	 * 
	 * @param endPoints Sorted array list of end points
	 * @return Root of the tree structure
	 */
	public static IntervalTreeNode buildTreeNodes(ArrayList<Integer> endPoints) {
		// COMPLETE THIS METHOD
		// THE FOLLOWING LINE HAS BEEN ADDED TO MAKE THE PROGRAM COMPILE
		
		Queue<IntervalTreeNode> queue = new Queue<IntervalTreeNode>();
		
		for(int p : endPoints) {
			IntervalTreeNode point = new IntervalTreeNode(p,p,p);	//MIGHT NEED TO USE NORMAL FORLOOP
			queue.enqueue(point);
		}
		
		while(true) {
			int s = queue.size();
			
			
			if(s == 1) {
				return queue.dequeue();
			}
			
			int temps = queue.size();
			while(temps > 1) {
				IntervalTreeNode T1 = queue.dequeue();
				IntervalTreeNode T2 = queue.dequeue();
				float v1 = T1.maxSplitValue;
				float v2 = T2.minSplitValue;
				
				float x = (v1 + v2)/2;
				
				IntervalTreeNode nodeN = new IntervalTreeNode(x, T1.minSplitValue, T2.maxSplitValue);
				
				nodeN.leftChild = T1;
				nodeN.rightChild = T2;
				
				queue.enqueue(nodeN);
				
				temps = temps - 2;
			}
			
			if(temps == 1)
				queue.enqueue(queue.dequeue());
		}
			
		
	}
	
	
	private IntervalTreeNode nodeToMap(float minValue, float maxValue) {

		return nodeToMapNext(minValue, maxValue, root);
	}
	
	private IntervalTreeNode nodeToMapNext(float leftEnd, float rightEnd, IntervalTreeNode root) {
		
		float splitValue = (leftEnd + rightEnd)/2;
		

		if (leftEnd <= root.splitValue && rightEnd >= root.splitValue) {
			return root;
		}
		
		if (splitValue > root.splitValue) {
			if (root.rightChild == null)
				return null;
			
				return this.nodeToMapNext(leftEnd, rightEnd, root.rightChild);
		} else {
			if (root.leftChild == null)
				return null;
			
				return this.nodeToMapNext(leftEnd, rightEnd, root.leftChild);
		}
	}
	
	private ArrayList<Interval> addToNode(ArrayList<Interval> node, Interval interval) {

		if (node == null)
		{
			node = new ArrayList<Interval>();
		}
		
      
		node.add(interval);
		
		return node;
	}
	
	public void mapIntervalsToTree(ArrayList<Interval> leftSortedIntervals, ArrayList<Interval> rightSortedIntervals) {
      
		for(int i = 0; i < leftSortedIntervals.size(); i++) {
			
			if (leftSortedIntervals.get(i) == null)
				break;
			
			IntervalTreeNode ptr = nodeToMap(leftSortedIntervals.get(i).leftEndPoint, leftSortedIntervals.get(i).rightEndPoint);
			ptr.leftIntervals = addToNode(ptr.leftIntervals, leftSortedIntervals.get(i));
		}
		
    
		for (int i = 0; i < rightSortedIntervals.size(); i++)
		{
			if (rightSortedIntervals.get(i) == null)
				break;
			
			IntervalTreeNode ptr = nodeToMap(rightSortedIntervals.get(i).leftEndPoint, rightSortedIntervals.get(i).rightEndPoint);
			ptr.rightIntervals = addToNode(ptr.rightIntervals, rightSortedIntervals.get(i));
		}

      
	}

	
	
	/**
	 * Maps a set of intervals to the nodes of this interval tree. 
	 * 
	 * @param leftSortedIntervals Array list of intervals sorted according to left endpoints
	 * @param rightSortedIntervals Array list of intervals sorted according to right endpoints
	 */

	/**
	 * Gets all intervals in this interval tree that intersect with a given interval.
	 * 
	 * @param q The query interval for which intersections are to be found
	 * @return Array list of all intersecting intervals; size is 0 if there are no intersections
	 */
	
	private ArrayList<Interval> findIntersectingIntervals(float min, float max, ArrayList<Interval> q)
	{
		ArrayList<Interval> resultList = new ArrayList<Interval>();
		
		if (q == null)
			return resultList;
		
		for (int i = 0; i < q.size(); i++)
		{			
			if (q.get(i) == null)
				break;
			
			Interval checkInterval = q.get(i);
			
			
			if ((checkInterval.leftEndPoint <= min && checkInterval.rightEndPoint >= min)
					|| (checkInterval.leftEndPoint <= max && checkInterval.rightEndPoint >= max)
					|| (checkInterval.leftEndPoint >= min && checkInterval.rightEndPoint <= max))
			{
				resultList.add(checkInterval);
			}
		}
		
		return resultList;
	}
	
	private ArrayList<Interval> findIntersectingIntervals(Interval q, IntervalTreeNode root) {
    
		ArrayList<Interval> resultList = new ArrayList<Interval>();
		float min = q.leftEndPoint;
		float max = q.rightEndPoint;
		
    
		if (root.leftChild == null && root.rightChild == null)
		{
			return resultList;
		}
		
   
		if (min <= root.splitValue && max >= root.splitValue)
		{
			if (root.leftIntervals != null)
				resultList.addAll(root.leftIntervals);
			
			if (root.leftChild != null)
				resultList.addAll(findIntersectingIntervals(q, root.leftChild));
			
			if (root.rightChild != null)
				resultList.addAll(findIntersectingIntervals(q, root.rightChild));
		}
     
		else if (max < root.splitValue) 
		{
			resultList.addAll(findIntersectingIntervals(min, max, root.leftIntervals));
			
			if (root.leftChild != null)
				resultList.addAll(findIntersectingIntervals(q, root.leftChild));
		}
     
		else if (min > root.splitValue) 
		{
			resultList.addAll(findIntersectingIntervals(min, max, root.rightIntervals));
			
			if (root.rightChild != null)
				resultList.addAll(findIntersectingIntervals(q, root.rightChild));

		}
			
		return resultList;
	}
	
	public ArrayList<Interval> findIntersectingIntervals(Interval q) {
		ArrayList<Interval> resultList = new ArrayList<Interval>();
		
		if (q == null)
		{
			return resultList;
		}
		
		resultList.addAll(this.findIntersectingIntervals(q, root));
		
		return resultList;
	}
	
	
}

