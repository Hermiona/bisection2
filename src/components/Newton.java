//package components;
//
//
//
//import java.applet.Applet;
//import java.awt.*;
//import info.lundin.math.*;
//import java.math.BigDecimal;
//import java.math.MathContext;
//import java.util.concurrent.TimeUnit;
//
//public class Newton extends Applet  implements Runnable {
//
//    TextField input;
//    TextField startValue;
//    TextField iter;
//    TextField tolTxt;
//    
//    TextArea display;
//    Button go;
//    Button stop;
//    Button contin;
//    Panel commonPanel;
//    Panel btnPanel;
//    Panel lblPanel;
//    Panel txtPanel;
//    Panel p4;
//    Panel p5;
//    Label lblEquation;
//    Label lblStartVal;
//    Label lblIterations;
//    Label lblTolerance;
//    Derive d;
//    Eval e;
//    Thread t;
//    BigDecimal xVal;
//    BigDecimal xPrev;
//    BigDecimal funcVal;
//    BigDecimal b_a;
//    BigDecimal tol;
//    
//    boolean loop = false;
//
//    @Override
//    public void init() {
//        d = new Derive();
//        e = new Eval();
//        input = new TextField();
//        startValue = new TextField();
//        iter = new TextField("10");
//        tolTxt=new TextField();
//        display = new TextArea(5, 30);
//        go = new Button("Start");
//        stop = new Button("Stop");
//        contin = new Button("Continue");
//
//        commonPanel = new Panel();
//        btnPanel = new Panel();
//        p4 = new Panel();
//        lblPanel = new Panel();
//        txtPanel = new Panel();
//        p5 = new Panel();
//
//        commonPanel.setBackground(Color.lightGray);
//        btnPanel.setBackground(Color.lightGray);
//        lblPanel.setBackground(Color.lightGray);
//        txtPanel.setBackground(Color.lightGray);
//        p4.setBackground(Color.lightGray);
//        p5.setBackground(Color.lightGray);
//
//        input.setBackground(Color.white);
//        startValue.setBackground(Color.white);
//        iter.setBackground(Color.white);
//        display.setBackground(Color.white);
//
//        lblEquation = new Label("Equation, f(x) = g(x)");
//        lblStartVal = new Label("Start value");
//        lblIterations = new Label("Number of iterations");
//        lblTolerance=new Label("Allowable error");
//        setLayout(new BorderLayout());
//
//        commonPanel.setLayout(new GridLayout(5, 1));
//        btnPanel.setLayout(new GridLayout(1, 5)); //for btns
//        lblPanel.setLayout(new GridLayout(1, 3)); //labels for startValue value and iterations
//        txtPanel.setLayout(new GridLayout(1, 4)); //txtfields for startValue value , iterations, tolerance
//
//        lblPanel.add(lblStartVal);
//        lblPanel.add(lblIterations);
//        lblPanel.add(lblTolerance);
//        txtPanel.add(startValue);
//        txtPanel.add(iter);
//        txtPanel.add(tolTxt);
//        btnPanel.add(p4);
//        btnPanel.add(go);
//        btnPanel.add(stop);
//        btnPanel.add(contin);
//        btnPanel.add(p5);
//        commonPanel.add(lblEquation);
//        commonPanel.add(input);
//        commonPanel.add(lblPanel);
//        commonPanel.add(txtPanel);
//        commonPanel.add(btnPanel);
//        add("North", commonPanel);
//        add("Center", display);
//    }
//
//    @Override
//    public boolean action(Event event, Object obj) {
//        if (event.target == go) {
//            startThread();
//            return true;
//        }
//        if (event.target == stop) {
//            loop = false;
//            return true;
//        }
//        if (event.target == contin) {
//            startValue.setText(String.valueOf(xVal));
//            startThread();
//            return true;
//        }
//
//        return false;
//    }
//
//    public void startThread() {
//        try {
//            t = new Thread(this);
//            t.start();
//        } catch (Exception exception) {
//            display.append("Error: this is a serious error, " + exception + "\n" + exception.getMessage());
//        }
//    }
//
//    @Override
//    public void stop() {
//        loop = false;
//        t = null;
//    }
//
//    @Override
//    public void run() {
//        boolean flag = false;
//        int k = 0, i = 0, maxIter = 0;
//        String s1,  derive;
//
//        loop = true;
//        go.setEnabled(false);
//        contin.setEnabled(false);
//
//        
//        
//        try {
//            display.append("\n");
//            s1 = input.getText().trim();
//
//            if (s1.equals("")) {
//                throw new Exception("No equation given");
//            }
//
//            xVal = new BigDecimal(startValue.getText().trim());
//            maxIter = Integer.parseInt(iter.getText().trim());
//
//            if ((i = s1.indexOf("=")) != -1) {
//                s1 = s1.substring(0, i) + "-(" + s1.substring(i + 1, s1.length()) + ")";
//            }
//            
//            long startTime = System.nanoTime(); 
//            
//            derive = d.diff(s1, "x")[0];
//            Expression expression = new Expression(s1);
//            Expression deriveExpression=new Expression(derive);
//            
//            tol=new BigDecimal(tolTxt.getText().trim());
//            funcVal=expression.with("x", xVal).eval();
//            
//            for (; k < maxIter && loop; k++) {
//                xPrev=xVal;
//                xVal =xVal.subtract(funcVal.divide(deriveExpression.with("x", xVal).eval(),  MathContext.DECIMAL128));  //xVal - e.eval(s1, "x=" + xVal) / e.eval(derive, "x=" + xVal);
//                funcVal=expression.with("x", xVal).eval();
//                
//                display.append((k + 1)  + ". x= " + xVal + ";  f(x)= " + funcVal + ";  abs(b-a)= "+ (xPrev.subtract(xVal)).abs() + "\n");
//                
//                if((xPrev.subtract(xVal)).abs().compareTo(tol)==-1){
//                    break;
//                }
//                
//            }
//            
//            long estimatedTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
//            
//            if(k>=maxIter)
//                display.append("Elapsed Time= " + estimatedTime + " ms. Maximum iterations reached\n");
//            else
//                display.append("Elapsed Time= " + estimatedTime + " ms.\n");
//            
//        } catch (NumberFormatException _ex) {
//            display.append("Error: please make sure  that a start value, the number of iterations \nand allowable error were given and are numbers \n\n");
//        } catch (Exception exception) {
//            display.append("Error: " + exception.getMessage() + "\n\n");
//        }
//
//        loop = false;
//        contin.setEnabled(true);
//        go.setEnabled(true);
//    }
//}
