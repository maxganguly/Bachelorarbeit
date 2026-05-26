package main.conditions;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import main.conditions.Condition.BOOLCOMPOUND;
import main.conditions.Condition.COMPARISON;
import main.conditions.Condition.NUMCOMPOUND;
import main.conditions.Condition.TYPE;

public class ConditionUtils {

	public static final Set<String> operators = Set.of(
			"+","-","*","/","%",
			"|","||","&","&&","^",
			"==","!=","<","<=",">",">=");
	
	public static Condition toCondition(String str, Set<String> variables) {
		if(str.isBlank())
			return ConditionElement.TRUE;
    	if(str.charAt(0) == '!') {
    		return new ConditionNot(toCondition(str.substring(
    				str.indexOf('(')+1,
    				str.lastIndexOf(')')), variables));
    	}
    	List<String> split = splitHighestElementsin3(str);
    	if(split.size() == 2) {
    		for(String s: operators) {
    			if(str.contains(s)) {
    				split = new LinkedList<String>();
    				split.add(str.substring(0,str.indexOf(s)-1));
    				split.add(str.substring(str.indexOf(s),str.indexOf(s)+s.length()));
    				split.add(str.substring(str.indexOf(s)+s.length()));
    				break;
    			}
    		}
    		if(split.size() != 3) {
    			return ConditionElement.newElement(str, TYPE.LITERAL);
    		}
    		System.err.println("Fucked UP");
    	}
    	else if (split.size() == 1) {
    		if(split.getFirst().contains("(")) {
    		String clean = str.substring(
					str.indexOf('(')+1,
					str.lastIndexOf(')'));
    		split = splitHighestElementsin3(clean);
    		if(split.size() != 3) {
    		if(str.charAt(0) == '(') {
    			return toCondition(clean, variables);
    		}
    		}
    		}else {
        		return ConditionElement.newElement(split.getFirst(), 
        				variables.contains(split.getFirst())?TYPE.VARIABLE:TYPE.LITERAL);
    		}
    	}
    	if(split.size() <= 1){
    		return ConditionElement.newElement(str, 
    				variables.contains(str)?TYPE.VARIABLE:TYPE.LITERAL);
    	}
    	String connector = split.get(1).trim();
    	Condition left = toCondition(split.getFirst(), variables);
    	Condition right = toCondition(split.getLast(), variables);
    	Object  operator = null;
    	operator = Condition.COMPARISON.fromString(connector);
    	if(operator != null)
    		return new ConditionComparison(left, (COMPARISON) operator, right);

    	operator = Condition.BOOLCOMPOUND.fromString(connector);
    	if(operator != null)
    		return new ConditionBoolCompound(left, (BOOLCOMPOUND) operator, right);
    	
    	operator = Condition.NUMCOMPOUND.fromString(connector);
    	if(operator != null)
    		return new ConditionNumCompound(left, (NUMCOMPOUND) operator, right);
    	    	
    	return ConditionElement.TRUE;
    }
    
	public static List<String> splitHighestElements(String str) {
    	LinkedList<String> parts = new LinkedList<String>();
    	int start = 0;
    	int parentheses = 0;
    	for(int i = 0; i < str.length();i++) {
    		if(str.charAt(i) == '(') {
    			parentheses++;
    		}
    		else if(str.charAt(i) == ')') {
    			parentheses--;
    		}else if(str.charAt(i) == ' ') {
    			if(parentheses == 0) {
    				if(start < i) {
        				parts.add(str.substring(start, i));
        				start = i+1;
        			}
    			}
    		}
    	}
    	parts.add(str.substring(start));
    	return parts;
    }
	
	public static List<String> splitHighestElementsin3(String str) {
    	LinkedList<String> parts = new LinkedList<String>();
    	int start = 0;
    	int parentheses = 0;
    	for(int i = 0; i < str.length();i++) {
    		if(str.charAt(i) == '(') {
    			parentheses++;
    		}
    		else if(str.charAt(i) == ')') {
    			parentheses--;
    		}else if(str.charAt(i) == ' ') {
    			if(parentheses == 0) {
    				if(start < i) {
        				parts.add(str.substring(start, i));
        				start = i+1;
        				if(parts.size()==2)
        					break;
        			}
    			}
    		}
    	}
    	parts.add(str.substring(start));
    	return parts;
    }
}
