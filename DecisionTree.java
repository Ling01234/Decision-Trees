import java.io.Serializable;
import java.util.ArrayList;
import java.text.*;
import java.lang.Math;

public class DecisionTree implements Serializable {

	DTNode rootDTNode;
	int minSizeDatalist; //minimum number of datapoints that should be present in the dataset so as to initiate a split
	
	// Mention the serialVersionUID explicitly in order to avoid getting errors while deserializing.
	public static final long serialVersionUID = 343L;
	
	public DecisionTree(ArrayList<Datum> datalist , int min) {
		minSizeDatalist = min;
		rootDTNode = (new DTNode()).fillDTNode(datalist);
	}

	class DTNode implements Serializable{
		//Mention the serialVersionUID explicitly in order to avoid getting errors while deserializing.
		public static final long serialVersionUID = 438L;
		boolean leaf;
		int label = -1;      // only defined if node is a leaf
		int attribute; // only defined if node is not a leaf
		double threshold;  // only defined if node is not a leaf

		DTNode left, right; //the left and right child of a particular node. (null if leaf)

		DTNode() {
			leaf = true;
			threshold = Double.MAX_VALUE;
		}

		
		// this method takes in a datalist (ArrayList of type datum). It returns the calling DTNode object 
		// as the root of a decision tree trained using the datapoints present in the datalist variable and minSizeDatalist.
		// Also, KEEP IN MIND that the left and right child of the node correspond to "less than" and "greater than or equal to" threshold
		DTNode fillDTNode(ArrayList<Datum> datalist) {
			
			
			if (datalist.size() < DecisionTree.this.minSizeDatalist) {
				DTNode node = new DTNode();
				
				node.leaf = true;
				node.label = findMajority(datalist);
				
				return node;
			}
			
			
			
			
			int test = 1;
			int l = datalist.get(0).y;
			
			
			for (Datum data : datalist) {
				
				if (data.y == l) {
					continue;
				}
				
				test = 0;
				break;
			}
			
			if (test == l) {
				DTNode node = new DTNode();
				
				node.leaf = true;
				node.label = l;
				return node;
			}
			
			
			double bestAvgEntropy = Double.MAX_VALUE;
			int bestAttribute = -1;
			double bestThreshold = -1;
			
			//ArrayList<Datum> data1 = new ArrayList<Datum>();
			//ArrayList<Datum> data2 = new ArrayList<Datum>();
			
			for (int index = 0; index < datalist.get(0).x.length; index++) {
				

				for (Datum data : datalist) {
					
					double splittingValue = data.x[index];
					
					ArrayList<Datum> first = new ArrayList<Datum>();
					ArrayList<Datum> second = new ArrayList<Datum>();
					
					
					//split into 2 sub-categories
					for (Datum tmpData : datalist) {
					
					if (tmpData.x[index] < splittingValue) {
						first.add(tmpData);
					}
					else {
						second.add(tmpData);
						}
					}
					
					double entropy1 = calcEntropy(first);
					double entropy2 = calcEntropy(second);
					double avgEntropy = first.size()/datalist.size()*entropy1 + second.size()/datalist.size()*entropy2;
					
					if (bestAvgEntropy > avgEntropy) {
						bestAvgEntropy = avgEntropy;
						bestAttribute = index;
						bestThreshold = data.x[index];
						
						//data1 = first;
						//data2 = second;
					}
				}
			}
			
			ArrayList<Datum> data1 = new ArrayList<Datum>();
			ArrayList<Datum> data2 = new ArrayList<Datum>();
			
			for (Datum dataPoint : datalist) {
				if (dataPoint.x[bestAttribute] < bestThreshold) {
					data1.add(dataPoint);
				}
				else {data2.add(dataPoint);}
			}
			
			DTNode node = new DTNode();

			
			node.leaf = false;
			node.attribute = bestAttribute;
			node.threshold = bestThreshold;
			

			
			node.left = fillDTNode(data1);
			node.right = fillDTNode(data2);
			
			
			return node; 
			
			
		}



		// This is a helper method. Given a datalist, this method returns the label that has the most
		// occurrences. In case of a tie it returns the label with the smallest value (numerically) involved in the tie.
		int findMajority(ArrayList<Datum> datalist) {
			
			int [] votes = new int[2];

			//loop through the data and count the occurrences of datapoints of each label
			for (Datum data : datalist)
			{
				votes[data.y]+=1;
			}
			
			if (votes[0] >= votes[1])
				return 0;
			else
				return 1;
		}




		// This method takes in a datapoint (excluding the label) in the form of an array of type double (Datum.x) and
		// returns its corresponding label, as determined by the decision tree
		int classifyAtNode(double[] xQuery) {
			
			if (this.leaf) {
				
				return this.label;
			}
			
			if (xQuery[this.attribute] < this.threshold) {
				return this.left.classifyAtNode(xQuery);
			}
			
			
			return this.right.classifyAtNode(xQuery);
		}


		//given another DTNode object, this method checks if the tree rooted at the calling DTNode is equal to the tree rooted
		//at DTNode object passed as the parameter
		public boolean equals(Object dt2)
		{
			if (!(dt2 instanceof DTNode)) {
				return false;
				}
			
			
				if (((DTNode)dt2).leaf == this.leaf) { 
					

					
					if (((DTNode) dt2).leaf == false) { //is an internal node
						boolean a = ((DTNode) dt2).threshold == this.threshold;
						boolean b = ((DTNode) dt2).attribute == this.attribute;
						
						if (!(a&&b)) {
							return false;
						}
						
						
						if (this.left != null && ((DTNode) dt2).left != null) {
							if (this.right != null && ((DTNode) dt2).right != null) {
							
							
							
						return this.left.equals(((DTNode) dt2).left) && this.right.equals(((DTNode) dt2).right);
						   }
						}
						
						
					}
					
					else { //is a leaf
						boolean c = ((DTNode) dt2).label == this.label;
						
						if(!c) {
							return false;
						}
						return true;
					}

				}
				
				
				return false;

		}
	}
	



	//Given a dataset, this returns the entropy of the dataset
	double calcEntropy(ArrayList<Datum> datalist) {
		double entropy = 0;
		double px = 0;
		float [] counter= new float[2];
		if (datalist.size()==0)
			return 0;
		double num0 = 0.00000001,num1 = 0.000000001;

		//calculates the number of points belonging to each of the labels
		for (Datum d : datalist)
		{
			counter[d.y]+=1;
		}
		//calculates the entropy using the formula specified in the document
		for (int i = 0 ; i< counter.length ; i++)
		{
			if (counter[i]>0)
			{
				px = counter[i]/datalist.size();
				entropy -= (px*Math.log(px)/Math.log(2));
			}
		}

		return entropy;
	}


	// given a datapoint (without the label) calls the DTNode.classifyAtNode() on the rootnode of the calling DecisionTree object
	int classify(double[] xQuery ) {
		return this.rootDTNode.classifyAtNode( xQuery );
	}

	// Checks the performance of a DecisionTree on a dataset
	// This method is provided in case you would like to compare your
	// results with the reference values provided in the PDF in the Data
	// section of the PDF
	String checkPerformance( ArrayList<Datum> datalist) {
		DecimalFormat df = new DecimalFormat("0.000");
		float total = datalist.size();
		float count = 0;

		for (int s = 0 ; s < datalist.size() ; s++) {
			double[] x = datalist.get(s).x;
			int result = datalist.get(s).y;
			if (classify(x) != result) {
				count = count + 1;
			}
		}

		return df.format((count/total));
	}


	//Given two DecisionTree objects, this method checks if both the trees are equal by
	//calling onto the DTNode.equals() method
	public static boolean equals(DecisionTree dt1,  DecisionTree dt2)
	{
		boolean flag = true;
		flag = dt1.rootDTNode.equals(dt2.rootDTNode);
		return flag;
	}

}
