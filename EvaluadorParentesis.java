import java.util.Stack;

public class EvaluadorParentesis {

    public boolean evaluar(String exp){
        String[] chars = exp.split("");
        Stack<String> parentesis = new Stack<>();
        for(String s:chars){
            switch (s){
                case "(":
                    parentesis.push(s);
                    break;
                case "[":
                    parentesis.push(s);
                    break;
                case "{":
                    parentesis.push(s);
                    break;
                case ")":
                    if (!parentesis.pop().equals("(")){
                        return false;
                    }
                    break;
                case "]":
                    if (!parentesis.pop().equals("[")){
                        return false;
                    }
                    break;
                case "}":
                    if (!parentesis.pop().equals("{")){
                        return false;
                    }
                    break;
            }
        }
        return parentesis.isEmpty();
    }

    public static void main(String[] args) {
        String e="{[}]";
        EvaluadorParentesis ev=new EvaluadorParentesis();
        System.out.print("La expresion "+e+" tiene parentesis balanceados? "+ev.evaluar(e));
    }
}
