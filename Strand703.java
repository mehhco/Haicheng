/*
 * 1. add the variable: length of strand
 * 2. fix the problem that the end of model is not smooth as other part
 * 3. add the control of material
 * 4. change the coordinate source table's value from direct numbers to parameters
 * 5. add the box selection and contact pair of slope strands
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.comsol.model.*;
import com.comsol.model.util.*;

/** Model exported on Jan 9 2020, 15:15 by COMSOL 5.4.0.388. */
public class Strand703 {

  public static Model run() {
	coordinatesFile071 cf = new coordinatesFile071(9, 6, 2, 0, 0.1, 56, 20, 200);
	cf.createFile(); 
	Model model = ModelUtil.create("Model");
    String N = cf.getNum_strand();
    String num_turn = cf.getNMString();
    String lt = cf.getLt();
    String h1 = cf.getH1();
    String b = cf.getB();
    String g_h = cf.getGH();
    String g_v = cf.getGV();
    String S = cf.getS();
    String L = cf.getL();
    double num_strands = Double.valueOf(N);
	int num_strand = (int) num_strands;
	int num_cord=cf.num_cord;
    
    /*
     * create a map to put the transfer relation between my order and comsol logic order
     */
    Map<Integer, Integer> transfer = new HashMap<Integer,Integer>(); 
	  int[] myOrder = new int[num_strand];
	  myOrder[0] = 1+num_strand/2;
	  for(int j=1;j<=(num_strand/2);j++) {
		  myOrder[j] = num_strand-j+1;
	  }
	  for(int j=(num_strand/2+1);j<num_strand;j++) {
		  myOrder[j]=j-(num_strand/2);
	  }
	  for(int j = 1; j <= num_strand;j++) {
		  transfer.put(j, myOrder[j-1]);
	  }
	  
	  	/*
	     * create a list to store the rectangular pos information
	     */
	    List<String[]> pos = new ArrayList<String[]>();	
	    String xpos;
	    String ypos="";
	    
	    int mid=num_strand/2+1;
	    System.out.println(mid);
	    for (int i = 1; i <= num_strand; i++) {
			  String[] posInfo = new String[2];
			  if(i<=mid) {
				  xpos = "0";
				  for(int j=1;j<=mid; j++) {
					  ypos = (i-1) +"*(b+g_v)";
				  }
			  }else {
				  xpos="h1+g_h";
				  for(int j=mid+1;j<=num_strand; j++) {
					  ypos = (i-mid-1) +"*(b+g_v)";
				  }  
			  }
			  posInfo[0] = xpos;
			  posInfo[1] = ypos; 
			  pos.add(posInfo);
		  }
		
	    /*
	     * create a String[] to store the rectangular size information
	     */
	   String[] recSize = new String[2];
	   recSize[0] = "h1";
	   recSize[1] = "b";
	    
	   
	   /*
	    * create a String[][] for box selection
	    */ 
	   int num_pairgroup = 0;	//number of pair groups in same length span
	   if(num_cord%2==0) {
		   num_pairgroup = (num_cord-2)/2;
	   }else if(num_cord%2!=0) {
		   num_pairgroup = (num_cord-2)/2+1;
	   }
	   String[][] box = new String[num_pairgroup][(mid-2)*6];
	   for (int i = 0; i < num_pairgroup; i++) {
		   if(i%2==0) {
			   for(int j = 0; j < (mid-2); j++) {
			   box[i][6*j+0]="0";
			   box[i][6*j+1]="h1";
			   box[i][6*j+2]=(j+1)+"*b";
			   box[i][6*j+3]=(j+2)+"*b";
			   box[i][6*j+4]="-"+(i+1)+"*(lt+S)-0.5";
			   box[i][6*j+5]="-"+(i+1)+"*(lt+S)+S+0.5";
		   }
	} else if(i%2==1) {
		for(int j = 0; j < (mid-2); j++) {
			   box[i][6*j+0]="h1+g_h";
			   box[i][6*j+1]="2*h1+g_h+0.5";
			   box[i][6*j+2]=(j+1)+"*b";
			   box[i][6*j+3]=(j+2)+"*b";
			   box[i][6*j+4]="-"+(i+1)+"*(lt+S)-0.5";
			   box[i][6*j+5]="-"+(i+1)+"*(lt+S)+S+0.5";
		   }
	}   
	   }  
	  
    model.modelPath("C:\\Users\\huhai\\Desktop\\CTC project\\polygoncoordinates");
    model.param().set("S", S+"[mm]", "transposition pitch");
    model.param().set("lt", lt+"[mm]", "transposition length");
    model.param().set("h1", h1+"[mm]", "width");
    model.param().set("b", b+"[mm]", "thickness");
    model.param().set("g_v", g_v+"[mm]", "vertical gap");
    model.param().set("g_h", g_h+"[mm]", "horizontal gap");
    model.param().set("L", L+"[mm]", "total length£¬ !!!cannot be edit");
    model.param().set("num_turn", num_turn, "number of turns£¬ !!!cannot be edit");
    
    model.component().create("comp1", true);

    model.component("comp1").geom().create("geom1", 3);

    model.component("comp1").mesh().create("mesh1");

    model.component("comp1").geom("geom1").lengthUnit("mm");
    model.component("comp1").geom("geom1").geomRep("comsol");
    model.component("comp1").geom("geom1").create("wp1", "WorkPlane");
    model.component("comp1").geom("geom1").feature("wp1").label("Work Plane 1");
    model.component("comp1").geom("geom1").feature("wp1").set("unite", true);
    
    
    /*
     * create Rectangulars
     */
    for (int i = 1; i <= num_strand; i++) {
    	model.component("comp1").geom("geom1").feature("wp1").geom().create("r"+i, "Rectangle");
        model.component("comp1").geom("geom1").feature("wp1").geom().feature("r"+i).label("Rectangle "+i);
        model.component("comp1").geom("geom1").feature("wp1").geom().feature("r"+i)
             .set("pos", pos.get(i-1));
        model.component("comp1").geom("geom1").feature("wp1").geom().feature("r"+i)
        .set("size", recSize); 
	}
    
    buildPandS(model, num_strand, cf.list3, transfer);
    
    model.component("comp1").geom("geom1").feature("fin").label("Form Assembly");
    model.component("comp1").geom("geom1").feature("fin").set("action", "assembly");
    model.component("comp1").geom("geom1").feature("fin").set("pairtype", "contact");
    model.component("comp1").geom("geom1").run();
    model.component("comp1").geom("geom1").run("fin");

    BoxSelection(model, box, num_pairgroup, mid-2);
    AddContact(model, num_pairgroup*(mid-2), num_strand);
    
    addMaterial(model);
    model.component("comp1").view("view2").tag("view11");

    model.component("comp1").physics().create("solid", "SolidMechanics", "geom1");

    model.component("comp1").view("view11").label("View 11");
    model.component("comp1").view("view11").axis().set("xmin", -13.55316162109375);
    model.component("comp1").view("view11").axis().set("xmax", 41.963096618652344);
    model.component("comp1").view("view11").axis().set("ymin", -15.141975402832031);
    model.component("comp1").view("view11").axis().set("ymax", 18.0716495513916);

    model.study().create("std1");
    model.study("std1").create("stat", "Stationary");

    return model;
  }

  public static void buildPandS(Model model, int n, List<String[][]> list,Map<Integer, Integer> transfer) { 
	  for (int i = 1; i <= n; i++) {
		  //here i refer to the label I defined in comsol, and that is the same to the key 
		  // and lable is the same as the interior order, namely  "pol5" and "Polygon 1"
		  model.component("comp1").geom("geom1").create("pol"+i, "Polygon");
		    model.component("comp1").geom("geom1").feature("pol"+i).label("Polygon "+i);
		    model.component("comp1").geom("geom1").feature("pol"+i).set("selresult", true);
		    model.component("comp1").geom("geom1").feature("pol"+i).set("source", "table");
		    model.component("comp1").geom("geom1").feature("pol"+i)
		         .set("table", list.get(i-1));
		    model.component("comp1").geom("geom1").create("swe"+i, "Sweep");
		    model.component("comp1").geom("geom1").feature("swe"+i).label("Sweep "+i);
		    model.component("comp1").geom("geom1").feature("swe"+i).set("selresult", true);
		    model.component("comp1").geom("geom1").feature("swe"+i).set("color", "20");
		    model.component("comp1").geom("geom1").feature("swe"+i).set("crossfaces", true);
		    model.component("comp1").geom("geom1").feature("swe"+i).set("includefinal", false);
		    model.component("comp1").geom("geom1").feature("swe"+i).set("parameterization", "normalizedarclength");
		    model.component("comp1").geom("geom1").feature("swe"+i).set("align", "adjustspine");
		    model.component("comp1").geom("geom1").feature("swe"+i).selection("face").set("wp1.uni", transfer.get(i));
		    model.component("comp1").geom("geom1").feature("swe"+i).selection("edge").named("pol"+i);
		    model.component("comp1").geom("geom1").feature("swe"+i).selection("diredge").set("pol"+i+"(1)", 1);
	} 	
  }
  
  public static void addMaterial(Model model) {

	    model.component("comp1").material().create("mat1", "Common");
	    model.component("comp1").material("mat1").label("1050 [solid]");
	    model.component("comp1").material("mat1").info().create("DIN");
	    model.component("comp1").material("mat1").info("DIN").body("Al99.5");
	    model.component("comp1").material("mat1").info().create("UNS");
	    model.component("comp1").material("mat1").info("UNS").body("A91050");
	    model.component("comp1").material("mat1").info().create("Composition");
	    model.component("comp1").material("mat1").info("Composition")
	         .body("99.5 Al min, 0.05 Cu max, 0.4 Fe max, 0.05 Mg max, 0.05 Mn max, 0.25 Si max, 0.05 V max (wt%)");
	    model.component("comp1").material("mat1").propertyGroup("def")
	         .set("thermalconductivity", "k_solid_1(T[1/K])[W/(m*K)]");
	    model.component("comp1").material("mat1").propertyGroup("def").set("resistivity", "res_solid_1(T[1/K])[ohm*m]");
	    model.component("comp1").material("mat1").propertyGroup("def")
	         .set("thermalexpansioncoefficient", "(alpha_solid_1(T[1/K])[1/K]+(Tempref-293[K])*if(abs(T-Tempref)>1e-3,(alpha_solid_1(T[1/K])[1/K]-alpha_solid_1(Tempref[1/K])[1/K])/(T-Tempref),d(alpha_solid_1(T[1/K])[1/K],T)))/(1+alpha_solid_1(Tempref[1/K])[1/K]*(Tempref-293[K]))");
	    model.component("comp1").material("mat1").propertyGroup("def")
	         .set("heatcapacity", "C_solid_1(T[1/K])[J/(kg*K)]");
	    model.component("comp1").material("mat1").propertyGroup("def")
	         .set("electricconductivity", "sigma_solid_1(T[1/K])[S/m]");
	    model.component("comp1").material("mat1").propertyGroup("def").set("HC", "HC_solid_1(T[1/K])[J/(mol*K)]");
	    model.component("comp1").material("mat1").propertyGroup("def").set("VP", "VP_solid_1(T[1/K])[Pa]");
	    model.component("comp1").material("mat1").propertyGroup("def").set("emissivity", "epsilon(T[1/K])");
	    model.component("comp1").material("mat1").propertyGroup("def").set("density", "rho_solid_1(T[1/K])[kg/m^3]");
	    model.component("comp1").material("mat1").propertyGroup("def").set("TD", "TD(T[1/K])[m^2/s]");
	    model.component("comp1").material("mat1").propertyGroup("def").func().create("k_solid_1", "Piecewise");
	    model.component("comp1").material("mat1").propertyGroup("def").func("k_solid_1").set("funcname", "k_solid_1");
	    model.component("comp1").material("mat1").propertyGroup("def").func("k_solid_1").set("arg", "T");
	    model.component("comp1").material("mat1").propertyGroup("def").func("k_solid_1").set("extrap", "constant");
	    model.component("comp1").material("mat1").propertyGroup("def").func("k_solid_1")
	         .set("pieces", new String[][]{{"293.0", "933.0", "39.64599+1.684012*T^1-0.005413421*T^2+8.431302E-6*T^3-6.537049E-9*T^4+2.002031E-12*T^5"}});
	    model.component("comp1").material("mat1").propertyGroup("def").func().create("res_solid_1", "Piecewise");
	    model.component("comp1").material("mat1").propertyGroup("def").func("res_solid_1")
	         .set("funcname", "res_solid_1");
	    model.component("comp1").material("mat1").propertyGroup("def").func("res_solid_1").set("arg", "T");
	    model.component("comp1").material("mat1").propertyGroup("def").func("res_solid_1").set("extrap", "constant");
	    model.component("comp1").material("mat1").propertyGroup("def").func("res_solid_1")
	         .set("pieces", new String[][]{{"1.0", "20.0", "1.091612E-12-1.10726E-13*T^1+3.696901E-14*T^2-2.781934E-15*T^3+1.008733E-16*T^4"}, 
	         {"20.0", "50.0", "-3.313487E-11+7.29041E-12*T^1-4.771551E-13*T^2+1.071535E-14*T^3"}, 
	         {"50.0", "200.0", "1.096556E-10-3.988929E-11*T^1+1.061978E-12*T^2-2.337666E-15*T^3"}, 
	         {"200.0", "933.0", "-1.037048E-8+1.451201E-10*T^1-8.192563E-14*T^2+6.619834E-17*T^3"}});
	    model.component("comp1").material("mat1").propertyGroup("def").func().create("alpha_solid_1", "Piecewise");
	    model.component("comp1").material("mat1").propertyGroup("def").func("alpha_solid_1")
	         .set("funcname", "alpha_solid_1");
	    model.component("comp1").material("mat1").propertyGroup("def").func("alpha_solid_1").set("arg", "T");
	    model.component("comp1").material("mat1").propertyGroup("def").func("alpha_solid_1").set("extrap", "constant");
	    model.component("comp1").material("mat1").propertyGroup("def").func("alpha_solid_1")
	         .set("pieces", new String[][]{{"20.0", "220.0", "1.371347E-5+7.808536E-8*T^1-2.568882E-10*T^2+3.615726E-13*T^3"}, {"220.0", "610.0", "5.760185E-6+1.707141E-7*T^1-6.548135E-10*T^2+1.220625E-12*T^3-1.064883E-15*T^4+3.535918E-19*T^5"}, {"610.0", "933.0", "1.9495E-5+9.630182E-9*T^1+9.462013E-13*T^2"}});
	    model.component("comp1").material("mat1").propertyGroup("def").func().create("C_solid_1", "Piecewise");
	    model.component("comp1").material("mat1").propertyGroup("def").func("C_solid_1").set("funcname", "C_solid_1");
	    model.component("comp1").material("mat1").propertyGroup("def").func("C_solid_1").set("arg", "T");
	    model.component("comp1").material("mat1").propertyGroup("def").func("C_solid_1").set("extrap", "constant");
	    model.component("comp1").material("mat1").propertyGroup("def").func("C_solid_1")
	         .set("pieces", new String[][]{{"100.0", "320.0", "-290.4161+11.181*T^1-0.04125401*T^2+7.112754E-5*T^3-4.60822E-8*T^4"}, {"320.0", "933.0", "595.6585+1.513029*T^1-0.002070065*T^2+1.303608E-6*T^3"}});
	    model.component("comp1").material("mat1").propertyGroup("def").func().create("sigma_solid_1", "Piecewise");
	    model.component("comp1").material("mat1").propertyGroup("def").func("sigma_solid_1")
	         .set("funcname", "sigma_solid_1");
	    model.component("comp1").material("mat1").propertyGroup("def").func("sigma_solid_1").set("arg", "T");
	    model.component("comp1").material("mat1").propertyGroup("def").func("sigma_solid_1").set("extrap", "constant");
	    model.component("comp1").material("mat1").propertyGroup("def").func("sigma_solid_1")
	         .set("pieces", new String[][]{{"1.0", "20.0", "1/(1.008733E-16*T^4-2.781934E-15*T^3+3.696901E-14*T^2-1.107260E-13*T+1.091612E-12)"}, 
	         {"20.0", "50.0", "1/(1.071535E-14*T^3-4.771551E-13*T^2+7.290410E-12*T-3.313487E-11)"}, 
	         {"50.0", "200.0", "1/(-2.337666E-15*T^3+1.061978E-12*T^2-3.988929E-11*T+1.0965563E-10)"}, 
	         {"200.0", "933.0", "1/(6.619834E-17*T^3-8.192563E-14*T^2+1.451201E-10*T-1.037048E-08)"}});
	    model.component("comp1").material("mat1").propertyGroup("def").func().create("HC_solid_1", "Piecewise");
	    model.component("comp1").material("mat1").propertyGroup("def").func("HC_solid_1").set("funcname", "HC_solid_1");
	    model.component("comp1").material("mat1").propertyGroup("def").func("HC_solid_1").set("arg", "T");
	    model.component("comp1").material("mat1").propertyGroup("def").func("HC_solid_1").set("extrap", "constant");
	    model.component("comp1").material("mat1").propertyGroup("def").func("HC_solid_1")
	         .set("pieces", new String[][]{{"100.0", "320.0", "-7.835862+0.3016802*T^1-0.001113095*T^2+1.919128E-6*T^3-1.243367E-9*T^4"}, {"320.0", "933.0", "16.07176+0.04082379*T^1-5.585347E-5*T^2+3.517331E-8*T^3"}});
	    model.component("comp1").material("mat1").propertyGroup("def").func().create("VP_solid_1", "Piecewise");
	    model.component("comp1").material("mat1").propertyGroup("def").func("VP_solid_1").set("funcname", "VP_solid_1");
	    model.component("comp1").material("mat1").propertyGroup("def").func("VP_solid_1").set("arg", "T");
	    model.component("comp1").material("mat1").propertyGroup("def").func("VP_solid_1").set("extrap", "constant");
	    model.component("comp1").material("mat1").propertyGroup("def").func("VP_solid_1")
	         .set("pieces", new String[][]{{"293.0", "933.0", "(exp((-1.734200e+04/T-7.927000e-01*log10(T)+1.233981e+01)*log(10.0)))*1.333200e+02"}});
	    model.component("comp1").material("mat1").propertyGroup("def").func().create("epsilon", "Piecewise");
	    model.component("comp1").material("mat1").propertyGroup("def").func("epsilon").set("funcname", "epsilon");
	    model.component("comp1").material("mat1").propertyGroup("def").func("epsilon").set("arg", "T");
	    model.component("comp1").material("mat1").propertyGroup("def").func("epsilon").set("extrap", "constant");
	    model.component("comp1").material("mat1").propertyGroup("def").func("epsilon")
	         .set("pieces", new String[][]{{"175.0", "700.0", "-0.006159012+1.209023E-4*T^1-1.728543E-7*T^2+1.274369E-10*T^3"}});
	    model.component("comp1").material("mat1").propertyGroup("def").func().create("rho_solid_1", "Piecewise");
	    model.component("comp1").material("mat1").propertyGroup("def").func("rho_solid_1")
	         .set("funcname", "rho_solid_1");
	    model.component("comp1").material("mat1").propertyGroup("def").func("rho_solid_1").set("arg", "T");
	    model.component("comp1").material("mat1").propertyGroup("def").func("rho_solid_1").set("extrap", "constant");
	    model.component("comp1").material("mat1").propertyGroup("def").func("rho_solid_1")
	         .set("pieces", new String[][]{{"20.0", "130.0", "2734.317-0.02751647*T^1+0.001016054*T^2-1.700864E-5*T^3+5.734155E-8*T^4"}, {"130.0", "933.0", "2736.893-0.006011681*T^1-7.012444E-4*T^2+1.3582E-6*T^3-1.367828E-9*T^4+5.177991E-13*T^5"}});
	    model.component("comp1").material("mat1").propertyGroup("def").func().create("TD", "Piecewise");
	    model.component("comp1").material("mat1").propertyGroup("def").func("TD").set("funcname", "TD");
	    model.component("comp1").material("mat1").propertyGroup("def").func("TD").set("arg", "T");
	    model.component("comp1").material("mat1").propertyGroup("def").func("TD").set("extrap", "constant");
	    model.component("comp1").material("mat1").propertyGroup("def").func("TD")
	         .set("pieces", new String[][]{{"293.0", "933.0", "5.850406E-5+4.372409E-7*T^1-1.684332E-9*T^2+2.848785E-12*T^3-2.339518E-15*T^4+7.401871E-19*T^5"}});
	    model.component("comp1").material("mat1").propertyGroup("def").addInput("temperature");
	    model.component("comp1").material("mat1").propertyGroup("def").addInput("strainreferencetemperature");
	    model.component("comp1").material("mat1").propertyGroup().create("ThermalExpansion", "Thermal expansion");
	    model.component("comp1").material("mat1").propertyGroup("ThermalExpansion")
	         .set("dL", "(dL_solid_1(T[1/K])-dL_solid_1(Tempref[1/K]))/(1+dL_solid_1(Tempref[1/K]))");
	    model.component("comp1").material("mat1").propertyGroup("ThermalExpansion")
	         .set("dLIso", "(dL_solid_1(T)-dL_solid_1(Tempref))/(1+dL_solid_1(Tempref))");
	    model.component("comp1").material("mat1").propertyGroup("ThermalExpansion").set("alphatan", "CTE(T[1/K])[1/K]");
	    model.component("comp1").material("mat1").propertyGroup("ThermalExpansion").set("alphatanIso", "CTE(T)");
	    model.component("comp1").material("mat1").propertyGroup("ThermalExpansion").func()
	         .create("dL_solid_1", "Piecewise");
	    model.component("comp1").material("mat1").propertyGroup("ThermalExpansion").func("dL_solid_1")
	         .set("funcname", "dL_solid_1");
	    model.component("comp1").material("mat1").propertyGroup("ThermalExpansion").func("dL_solid_1").set("arg", "T");
	    model.component("comp1").material("mat1").propertyGroup("ThermalExpansion").func("dL_solid_1")
	         .set("extrap", "constant");
	    model.component("comp1").material("mat1").propertyGroup("ThermalExpansion").func("dL_solid_1")
	         .set("pieces", new String[][]{{"20.0", "188.0", "-0.004116601-4.000347E-6*T^1+5.370388E-8*T^2+3.714324E-10*T^3-1.45073E-12*T^4"}, {"188.0", "933.0", "-0.006312089+2.156284E-5*T^1-4.744254E-9*T^2+1.811015E-11*T^3-7.336673E-15*T^4"}});
	    model.component("comp1").material("mat1").propertyGroup("ThermalExpansion").func().create("CTE", "Piecewise");
	    model.component("comp1").material("mat1").propertyGroup("ThermalExpansion").func("CTE").set("funcname", "CTE");
	    model.component("comp1").material("mat1").propertyGroup("ThermalExpansion").func("CTE").set("arg", "T");
	    model.component("comp1").material("mat1").propertyGroup("ThermalExpansion").func("CTE")
	         .set("extrap", "constant");
	    model.component("comp1").material("mat1").propertyGroup("ThermalExpansion").func("CTE")
	         .set("pieces", new String[][]{{"20.0", "79.0", "-3.317274E-6+3.068688E-7*T^1-1.004816E-8*T^2+1.724768E-10*T^3-8.846061E-13*T^4"}, {"79.0", "230.0", "-2.288239E-5+6.674915E-7*T^1-4.402622E-9*T^2+1.455358E-11*T^3-1.910622E-14*T^4"}, {"230.0", "900.0", "1.243109E-5+5.050772E-8*T^1-5.806556E-11*T^2+3.014305E-14*T^3"}});
	    model.component("comp1").material("mat1").propertyGroup("ThermalExpansion").addInput("temperature");
	    model.component("comp1").material("mat1").propertyGroup("ThermalExpansion")
	         .addInput("strainreferencetemperature");
	    model.component("comp1").material("mat1").propertyGroup().create("Enu", "Young's modulus and Poisson's ratio");
	    model.component("comp1").material("mat1").propertyGroup("Enu").set("youngsmodulus", "E(T[1/K])[Pa]");
	    model.component("comp1").material("mat1").propertyGroup("Enu").set("poissonsratio", "nu(T[1/K])");
	    model.component("comp1").material("mat1").propertyGroup("Enu").func().create("E", "Piecewise");
	    model.component("comp1").material("mat1").propertyGroup("Enu").func("E").set("funcname", "E");
	    model.component("comp1").material("mat1").propertyGroup("Enu").func("E").set("arg", "T");
	    model.component("comp1").material("mat1").propertyGroup("Enu").func("E").set("extrap", "constant");
	    model.component("comp1").material("mat1").propertyGroup("Enu").func("E")
	         .set("pieces", new String[][]{{"0.0", "773.0", "7.770329E10+2036488.0*T^1-189160.7*T^2+425.2931*T^3-0.3545736*T^4"}});
	    model.component("comp1").material("mat1").propertyGroup("Enu").func().create("nu", "Piecewise");
	    model.component("comp1").material("mat1").propertyGroup("Enu").func("nu").set("funcname", "nu");
	    model.component("comp1").material("mat1").propertyGroup("Enu").func("nu").set("arg", "T");
	    model.component("comp1").material("mat1").propertyGroup("Enu").func("nu").set("extrap", "constant");
	    model.component("comp1").material("mat1").propertyGroup("Enu").func("nu")
	         .set("pieces", new String[][]{{"0.0", "773.0", "0.3238668+3.754548E-6*T^1+2.213647E-7*T^2-6.565023E-10*T^3+4.21277E-13*T^4+3.170505E-16*T^5"}});
	    model.component("comp1").material("mat1").propertyGroup("Enu").addInput("temperature");
	    model.component("comp1").material("mat1").propertyGroup().create("KG", "Bulk modulus and shear modulus");
	    model.component("comp1").material("mat1").propertyGroup("KG").set("G", "mu(T[1/K])[Pa]");
	    model.component("comp1").material("mat1").propertyGroup("KG").set("K", "kappa(T[1/K])[Pa]");
	    model.component("comp1").material("mat1").propertyGroup("KG").func().create("mu", "Piecewise");
	    model.component("comp1").material("mat1").propertyGroup("KG").func("mu").set("funcname", "mu");
	    model.component("comp1").material("mat1").propertyGroup("KG").func("mu").set("arg", "T");
	    model.component("comp1").material("mat1").propertyGroup("KG").func("mu").set("extrap", "constant");
	    model.component("comp1").material("mat1").propertyGroup("KG").func("mu")
	         .set("pieces", new String[][]{{"0.0", "773.0", "2.936605E10-121772.0*T^1-70037.91*T^2+160.9718*T^3-0.1368524*T^4"}});
	    model.component("comp1").material("mat1").propertyGroup("KG").func().create("kappa", "Piecewise");
	    model.component("comp1").material("mat1").propertyGroup("KG").func("kappa").set("funcname", "kappa");
	    model.component("comp1").material("mat1").propertyGroup("KG").func("kappa").set("arg", "T");
	    model.component("comp1").material("mat1").propertyGroup("KG").func("kappa").set("extrap", "constant");
	    model.component("comp1").material("mat1").propertyGroup("KG").func("kappa")
	         .set("pieces", new String[][]{{"0.0", "773.0", "7.35752E10+1900026.0*T^1-72136.97*T^2+53.47482*T^3"}});
	    model.component("comp1").material("mat1").propertyGroup("KG").addInput("temperature");
	    model.component("comp1").material("mat1").set("family", "aluminum");
  }
  
  public static void BoxSelection(Model model, String[][] box, int num_pairgroup, int num_faces) {
	  int s = 1;
	  for(int i = 0; i < num_pairgroup; i++) {
		  for(int j = 0; j < num_faces; j++) {
			  	model.component("comp1").selection().create("box"+s, "Box");
			  	model.component("comp1").selection("box"+s).label("Box"+i+j);
			  	model.component("comp1").selection("box"+s).set("entitydim", 2);
			    model.component("comp1").selection("box"+s).set("xmin", box[i][6*j+0]);
			    model.component("comp1").selection("box"+s).set("xmax", box[i][6*j+1]);
			    model.component("comp1").selection("box"+s).set("ymin", box[i][6*j+2]);
			    model.component("comp1").selection("box"+s).set("ymax", box[i][6*j+3]);
			    model.component("comp1").selection("box"+s).set("zmin", box[i][6*j+4]);
			    model.component("comp1").selection("box"+s).set("zmax", box[i][6*j+5]);
			    model.component("comp1").selection("box"+s).set("condition", "allvertices");  
			    s++;
		  } 	
	  }   
  }
  
  public static void AddContact(Model model, int num_box, int num_strand) {
	  for (int j = 0; j < num_box; j++) { 
		model.component("comp1").pair().create("p"+(2*num_strand+j), "Contact");
	    model.component("comp1").pair("p"+(2*num_strand+j)).source().named("box"+(j+1));
	    model.component("comp1").pair("p"+(2*num_strand+j)).destination().named("box"+(j+1));
	} 
  } 
  
  public static void main(String[] args) {
    run();
  }

}
