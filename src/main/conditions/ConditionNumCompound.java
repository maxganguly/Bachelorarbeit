package main.conditions;

import java.awt.IllegalComponentStateException;

public class ConditionNumCompound implements Condition {

	private Condition left,right;
	private Condition.NUMCOMPOUND compound;

	public ConditionNumCompound(Condition left, Condition.NUMCOMPOUND compound, Condition right) {
		this.left = left;
		this.right = right;
		this.compound = compound;
	}

	@Override
	public String toString() {
		return "("+left.toString() + " " + compound.toString() + " " + right.toString()+")";
	}

	@Override
	public TYPE getType() {
		return Condition.TYPE.COMPOUND;
	}

	@Override
	public Condition negate() {
		throw new IllegalComponentStateException("A numerical value cant be negated");
	}

	@Override
	public Condition evaluate() {
		Condition left = this.left.evaluate();
		Condition right = this.right.evaluate();
		if(left instanceof ConditionElement && right instanceof ConditionElement){
			ConditionElement cleft = (ConditionElement) left;
			ConditionElement cright = (ConditionElement) right;
			if(cleft.type == TYPE.VARIABLE || cright.type == TYPE.VARIABLE) {
				return new ConditionNumCompound(left, compound, right);
			}
			//int
			try {
				int l = Integer.parseInt(cleft.value);
				int r = Integer.parseInt(cright.value);
				switch(compound) {
				case ADDITION : return ConditionElement.newElement(""+(l+r),TYPE.LITERAL);
				case SUBTRACTION : return ConditionElement.newElement(""+(l-r),TYPE.LITERAL);
				case MULTIPLICATION : return ConditionElement.newElement(""+(l*r),TYPE.LITERAL);
				case DIVISION : return ConditionElement.newElement(""+(l/r),TYPE.LITERAL);
				case MOD : return ConditionElement.newElement(""+(l%r),TYPE.LITERAL);
				}
			} catch (NumberFormatException e) {}
			//double
			try {
				double l = Double.parseDouble(cleft.value);
				double r = Double.parseDouble(cright.value);
				switch(compound) {
				case ADDITION : return ConditionElement.newElement(""+(l+r),TYPE.LITERAL);
				case SUBTRACTION : return ConditionElement.newElement(""+(l-r),TYPE.LITERAL);
				case MULTIPLICATION : return ConditionElement.newElement(""+(l*r),TYPE.LITERAL);
				case DIVISION : return ConditionElement.newElement(""+(l/r),TYPE.LITERAL);
				case MOD : return ConditionElement.newElement(""+(l%r),TYPE.LITERAL);
				}
			} catch (NumberFormatException e) {}
		}
		else if(right instanceof ConditionElement && left instanceof ConditionNumCompound){
			ConditionElement element1;
			ConditionNumCompound compound1;
				element1 = (ConditionElement) right;
				compound1 = (ConditionNumCompound) left;
			
			
			if(compound1.right instanceof ConditionElement) {
				return simplify1(compound1.left, compound1.compound, (ConditionElement)compound1.right, compound, element1);
			}
			//commutative
			if(compound1.left instanceof ConditionElement && 
					(compound1.compound == NUMCOMPOUND.ADDITION || compound1.compound == NUMCOMPOUND.MULTIPLICATION)) {
				return simplify1(compound1.right, compound1.compound, (ConditionElement)compound1.left, compound, element1);
			}
		}
		else if(left instanceof ConditionElement && right instanceof ConditionNumCompound &&
				(compound == NUMCOMPOUND.ADDITION || compound==NUMCOMPOUND.MULTIPLICATION)){
			ConditionElement element1;
			ConditionNumCompound compound1;
				element1 = (ConditionElement) left;
				compound1 = (ConditionNumCompound) right;
			
			
			if(compound1.right instanceof ConditionElement) {
				return simplify1(compound1.left, compound1.compound, (ConditionElement)compound1.right, compound, element1);
			}
			//commutative
			if(compound1.left instanceof ConditionElement && 
					(compound1.compound == NUMCOMPOUND.ADDITION || compound1.compound == NUMCOMPOUND.MULTIPLICATION)) {
				return simplify1(compound1.right, compound1.compound, (ConditionElement)compound1.left, compound, element1);
			}
		}
		return this;
	}
	/**
	 * ( base compound1 element1) compound2 element2
	 * @param base
	 * @param compound1
	 * @param element1
	 * @param compound2
	 * @param element2
	 * @return
	 */
	private ConditionNumCompound simplify1(Condition base, Condition.NUMCOMPOUND compound1, ConditionElement element1,
			Condition.NUMCOMPOUND compound2, ConditionElement element2) {
			try {
				double e1 = Integer.parseInt(element1.value);
				if(compound1==NUMCOMPOUND.SUBTRACTION)
					e1*=-1;
				else if(compound1==NUMCOMPOUND.DIVISION)
					e1 = 1/e1;
	
				double e2 = Integer.parseInt(element2.value);
	
				switch(compound2) {
				case ADDITION : 
					return new ConditionNumCompound(base, compound1,  
							ConditionElement.newElement(""+(int)(e1+e2),TYPE.LITERAL));
				case SUBTRACTION : 
					return new ConditionNumCompound(base, compound1,  
							ConditionElement.newElement(""+(int)(-e1+e2),TYPE.LITERAL));
				case MULTIPLICATION : 
					return new ConditionNumCompound(base, compound1,  
							ConditionElement.newElement(""+((int)e1*e2),TYPE.LITERAL));
				case DIVISION : 
					return new ConditionNumCompound(base, compound1,  
							ConditionElement.newElement(""+(int)((1/e1)*e2),TYPE.LITERAL));
				case MOD : 
					return new ConditionNumCompound(base, compound1,  
							ConditionElement.newElement(""+kgv((int)e1,(int)e2),TYPE.LITERAL));
				}
			} catch (NumberFormatException e) {}
			//double
			try {
				double e1 = Double.parseDouble(element1.value);
				if(compound1==NUMCOMPOUND.SUBTRACTION)
					e1*=-1;
				else if(compound1==NUMCOMPOUND.DIVISION)
					e1 = 1/e1;
				double e2 = Double.parseDouble(element2.value);
				switch(compound1) {
				case ADDITION : 
					return new ConditionNumCompound(base, compound1,  
							ConditionElement.newElement(""+(e1+e2),TYPE.LITERAL));
				case SUBTRACTION : 
					return new ConditionNumCompound(base, compound1,  
							ConditionElement.newElement(""+(e1+e2),TYPE.LITERAL));
				case MULTIPLICATION : 
					return new ConditionNumCompound(base, compound1,  
							ConditionElement.newElement(""+(e1*e2),TYPE.LITERAL));
				case DIVISION : 
					return new ConditionNumCompound(base, compound1,  
							ConditionElement.newElement(""+(e1*e2),TYPE.LITERAL));
				case MOD : 
					return new ConditionNumCompound(base, compound1,  
							ConditionElement.newElement(""+kgv((int)e1,(int)e2),TYPE.LITERAL));
				}
			} catch (NumberFormatException e) {}
			return new ConditionNumCompound(
					new ConditionNumCompound(base, compound1, element1), compound2, element2);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == this)
			return true;
		if(!(obj instanceof ConditionNumCompound)) {
			return false;
		}
		ConditionNumCompound e = (ConditionNumCompound) obj;
		return left.equals(e.left) && compound==e.compound && right.equals(right);
	}
	
	public static int kgv(int a, int b) {
        int c;
        int k = 0;
        int r;
        int kgv;
        
        if (a < b) {
            c = a;
            a = b;
            b = c;
        }
        do {
            k = k + 1;
            kgv = a * k;
            r = kgv % b;
        }
        while (r != 0);
        return kgv;
	}

}