import java.text.DecimalFormat;

/**
 * change the coordinate source table's value from direct numbers to parameters
 * @author HHC
 *
 */
import java.util.ArrayList;
import java.util.List;
public class coordinatesFile071 {
	private double num_strand;	//number of strands 
	private double h1;	//width of the strand
	private double lt;	//transposition length
	private double b;	//height of the strand
	private double g_v;	//verticle gap 
	private double g_h;	//horizontal gap
	private double S;	//transpositon pitch
	private double L; 	//length of the conductor, has to be longer than 1 single turn, 
						//by which means longer than 2*(lt+S)
	int num_turn;
	int num_cord;
	public int getNMint() {
		return this.num_turn;
	}
	public String getNMString() {
		return String.valueOf(num_turn);
	}
	public double getH1double() {
		return this.h1;
	}

	public double getBdouble() {
		return this.b;
	}

	public double getGVdouble() {
		return this.g_v;
	}
	public double getGHdouble() {
		return this.g_h;
	}
	
	public String getNum_strand() {
		return String.valueOf(num_strand);
	}
	
	public String getH1() {
		return String.valueOf(h1);
	}
	
	public String getLt() {
		return String.valueOf(lt);
	}
	
	public String getB() {
		return String.valueOf(b);
	}
	
	public String getGV() {
		return String.valueOf(g_v);
	}
	
	public String getGH() {
		return String.valueOf(g_h);
	}
	
	public String getS() {
		return String.valueOf(S);
	}
	
	public String getL() {
		return String.valueOf(L);
	}

	List<String[][]>  list = new ArrayList<String[][]>();
	List<String[][]>  list2 = new ArrayList<String[][]>();
	List<String[][]>  list3 = new ArrayList<String[][]>();
	 
	int end_part;
	//list stores n tables n=5, 7, 9, 11бнбн
	//every table contains the coordinate information of single strand
	public void createFile() {
		num_turn = (int) (L/(2*(lt+S)));  //number of turns
		double left = L%(2*(lt+S));		//length left after complete turns 
		if(left>0&&left<=lt) {
			end_part=1;
		}else if(left>lt && left<=(lt+S)) {
			end_part=2;
		}else if (left>(lt+S) && left<=(2*lt+S)) {
			end_part=3;
		}else {
			end_part=4;
		}
		
		
		
		/**
		 * for one single turn, the coordinate can be defined as follows
		 * 5 piece of coordinate information about x, y, z are needed
		 */
		DecimalFormat df = new DecimalFormat( "0.00"); 
		//double[] z_coordinate = new double[num_turn*4+1+end_part];
		String[] z_coordinate = new String[num_turn*4+1+end_part];
		for (int i = 1; i <= num_strand; i++) { 
//			double[] x_coordinated = new double[5]; //define for first turn
//			double[] y_coordinated = new double[5]; 
			String[] x_coordinated = new String[5]; //define for first turn
			String[] y_coordinated = new String[5]; 
			/*
			 * define y coordinate
			 */ 
			if (i<(int)Math.ceil(num_strand/2)) { 
				//define y coordinate for polygon [1,2,бнбн n/2)
				for(int j=0;j<=3;j++) {
					//y_coordinated[j] = (Math.ceil(num_strand/2)-i) + * (g_v+b);
					y_coordinated[j] = (Math.ceil(num_strand/2)-i) + "*(g_v+b)";
				}
//				y_coordinated[4] =  (Math.ceil(num_strand/2)-i-1) * (g_v+b);
				y_coordinated[4] =  (Math.ceil(num_strand/2)-i-1) + "*(g_v+b)";
				
				//define for x coordinate of polygon [1,2,бнбн n/2)
				if(i == 1) {
					x_coordinated[0] = "0";
					for (int j = 1; j <=4; j++) {
						x_coordinated[j] = "h1+g_h";
					} 
				}else {
					for (int j = 0; j <=4; j++) {
						x_coordinated[j] = "h1+g_h";
					} 
				}
				 
			} 
			//define x,y coordinate for polygon n/2
			if (i==(int)Math.ceil(num_strand/2) ) {  
				for(int j=0;j<=4;j++) {
					y_coordinated[j] = "0";
				}
				for (int j = 0; j < 3; j++) {
					x_coordinated[j] = "h1+g_h";
				} 
				x_coordinated[3] = "0";
				x_coordinated[4] = "0";
			}
			
			//define x,y coordinate for polygon (n/2,+1,+2]
			if (i>Math.ceil(num_strand/2)) {  
				
				for(int j=0;j<=4;j++) {
					y_coordinated[j] = "0";
				}  
				for(int j=2;j<=4;j++) {
					y_coordinated[j] = (int) (i - (Math.ceil(num_strand/2))) +"*(g_v+b)";
				}
				y_coordinated[0] = (i-(Math.ceil(num_strand/2))-1) +"*(g_v+b)";
				y_coordinated[1] = (i-(Math.ceil(num_strand/2))-1) +"*(g_v+b)"; 
			}  
			/*
			 * define x coordinate
			 */ 
			//define for x coordinate of polygon [2бнбнn/2)
			if(i>1 && i<Math.ceil(num_strand/2))
				for (int j = 0; j <=4; j++) {
					x_coordinated[j] = "h1+g_h";
				}
			
			//define for x coordinate of polygon (n/2бнбн]
			if(i>Math.ceil(num_strand/2))
				for (int j = 0; j <=4; j++) {
					x_coordinated[j] = "0";
				}
			
			//generate z coordinate 
			z_coordinate[0]="0";
			for (int j = 1; j < z_coordinate.length; j++) {
				if(j%2 != 0) {
					z_coordinate[j]= z_coordinate[j-1]+"-lt";
				}
				else {
					z_coordinate[j]= z_coordinate[j-1]+"-S";
				} 
			}
			z_coordinate[num_turn*4+1+end_part-1]="-L";
			
			//store the coordinate infomation on list
			String[][] table = new String[5][3];
			for (int j = 0; j < 5; j++) { 
					table[j][0] = x_coordinated[j];
					table[j][1] = y_coordinated[j];
					table[j][2] = z_coordinate[j];		
			}
			list.add(table); //after loop, list will contain initial table (length =  1 turn)
		}
		
		list2.addAll(list);
		String[][] table1=new String[(num_turn)*4+1+end_part][3];
		//the number of nodes should be equals to (num_turn)*4+1+end_part
		for(int i=0;i<num_strand;i++) {   //to get the node file for every strand
				System.arraycopy(list.get(i),0,table1,0,1);  //the first node
				int count = 0;
				 //from node 2 to the end of last-1 turn
				for(int n = i; n<i+num_turn ; n++) { 
					System.arraycopy(list.get((int)(n%num_strand)), 1, table1, count*4+1, 4);
					count++;
					}
				
				 //last turn
				 for(int j=1;j<end_part;j++) {
					System.arraycopy(list.get((int)((i+num_turn)%num_strand)), j, 
							table1, count*4+j, 1); 
				  }
				 String[][] u1 = list.get((int)((i+num_turn)%num_strand));
				 String[] u11 = u1[end_part-1];
				 String[] u22 = u1[end_part];
				 
				 
					 double end_length=0.0;
					 String Send_length;
					 double ratio = 1;
					 String Sratio;
				 	if(end_part==1) {
					 end_length=L-num_turn*(2*(lt+S));
					 Send_length = "L-num_turn*(2*(lt+S))";
					 ratio = end_length/lt;
					 Sratio = Send_length+"/lt";
					 }else if(end_part==2) {
						 end_length=L-num_turn*(2*(lt+S))-lt;
						 Send_length = "L-num_turn*(2*(lt+S))-lt";
						 ratio = end_length/S;
						 Sratio = Send_length+"/S";
					 }else if(end_part==3) {
						 end_length=L-num_turn*(2*(lt+S))-lt-S;
						 Send_length = "L-num_turn*(2*(lt+S))-lt-S";
						 ratio = end_length/lt;
						 Sratio = Send_length+"/lt";
					 }else {
						 end_length=L-num_turn*(2*(lt+S))-2*lt-S;
						 Send_length = "L-num_turn*(2*(lt+S))-2*lt-S";
						 ratio = end_length/S;
						 Sratio = Send_length+"/S";
					 }
					 String x_end = u11[0];
					 String y_end = u11[1];
					 String x_end2 = u22[0];
					 String y_end2 = u22[1]; 
//					 double x_difference = (Double.valueOf(x_end2)-Double.valueOf(x_end))*ratio;
//					 double y_difference = (Double.valueOf(y_end2)-Double.valueOf(y_end))*ratio; 
//					 double x = x_difference+Double.valueOf(x_end);
//					 double y = y_difference+Double.valueOf(y_end);
					 //String x_final = String.valueOf(df.format(x));
					 String x_final ="("+x_end2+"-"+x_end+")*"+ratio+"+"+x_end;
					 //String y_final = String.valueOf(df.format(y));
					 String y_final ="("+y_end2+"-"+y_end+")*"+ratio+"+"+y_end;
				 
				String[][] table2 = new String[(num_turn)*4+1+end_part][3];
				num_cord=(num_turn)*4+1+end_part;
				for (int j = 0; j <= (num_turn)*4+end_part; j++) { 
						table2[j][0] = table1[j][0]; 
						table2[j][1] = table1[j][1]; 
						table2[j][2] = z_coordinate[j]; 
				} 
				table2[(num_turn)*4+end_part][0]=x_final;
				table2[(num_turn)*4+end_part][1]=y_final;
				list3.add(table2); 
				
			}
		}
	
	
	public coordinatesFile071(double num_strand, double h1, double b, double g_v, double g_h, double s, double lt, double L) { 
		super();
		this.num_strand = num_strand;
		this.h1 = h1;
		this.b = b;
		this.g_v = g_v;
		this.g_h = g_h;
		this.lt =lt;
		S = s;
		this.L=L;
		 
	}
	
	
	
	
	
}
