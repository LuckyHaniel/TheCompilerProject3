package main.app.develop.test_app;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.lang.String;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Stack;

import java.util.Collections;
import java.util.Stack;



/**
 *  四则混合算法借鉴于网上 来自：
 *                              http://www.cnblogs.com/woider/p/5331391.html
 *
 */

 /**
 *  算数表达式求值
 *  直接调用Calculator的类方法conversion()
 *  传入算数表达式，将返回一个浮点值结果
 *  如果计算过程错误，将返回一个NaN
 */
class Calculator {
     // 后缀式栈
    private Stack<String> postfixStack = new Stack<String>();
     // 运算符栈
    private Stack<Character> opStack = new Stack<Character>();
     // 运用运算符ASCII码-40做索引的运算符优先级
    private int[] operatPriority = new int[] { 0, 3, 2, 1, -1, 1, 0, 2 };

    public static double conversion(String expression) {
        double result = 0;
        Calculator cal = new Calculator();
        try {
            //  格式化表达式
            expression = transform(expression);
            //  运算
            result = cal.calculate(expression);
        } catch (Exception e) {
            // e.printStackTrace();
            // 运算错误返回NaN
            return 0.0 / 0.0;
        }
        // return new String().valueOf(result);
        return result;
    }

    /**
     * 将表达式中负数的符号更改
     *
     * @param expression
     *            例如-2+-1*(-3E-2)-(-1) 被转为 ~2+~1*(~3E~2)-(~1)
     */
    private static String transform(String expression) {
        // 该App并无负数的输入，因此不部分舍去
        return expression;
    }

    /**
     * 按照给定的表达式计算
     *
     * @param expression
     *            要计算的表达式例如:5+12*3+5/7
     * @return
     */
    public double calculate(String expression) {
        Stack<String> resultStack = new Stack<String>();
        prepare(expression);
        Collections.reverse(postfixStack);// 将后缀式栈反转
        String firstValue, secondValue, currentValue;// 参与计算的第一个值，第二个值和算术运算符
        while (!postfixStack.isEmpty()) {
            currentValue = postfixStack.pop();
            if (!isOperator(currentValue.charAt(0))) {// 如果不是运算符则存入操作数栈中
                currentValue = currentValue.replace("~", "-");
                resultStack.push(currentValue);
            } else {// 如果是运算符则从操作数栈中取两个值和该数值一起参与运算
                secondValue = resultStack.pop();
                firstValue = resultStack.pop();

                // 将负数标记符改为负号
                firstValue = firstValue.replace("~", "-");
                secondValue = secondValue.replace("~", "-");

                String tempResult = calculate(firstValue, secondValue, currentValue.charAt(0));
                resultStack.push(tempResult);
            }
        }
        return Double.valueOf(resultStack.pop());
    }

    /**
     * 数据准备阶段将表达式转换成为后缀式栈
     *
     * @param expression
     */
    private void prepare(String expression) {
        opStack.push(',');// 运算符放入栈底元素逗号，此符号优先级最低
        char[] arr = expression.toCharArray();
        int currentIndex = 0;// 当前字符的位置
        int count = 0;// 上次算术运算符到本次算术运算符的字符的长度便于或者之间的数值
        char currentOp, peekOp;// 当前操作符和栈顶操作符
        for (int i = 0; i < arr.length; i++) {
            currentOp = arr[i];
            if (isOperator(currentOp)) {// 如果当前字符是运算符
                if (count > 0) {
                    postfixStack.push(new String(arr, currentIndex, count));// 取两个运算符之间的数字
                }
                peekOp = opStack.peek();
                if (currentOp == ')') {// 遇到反括号则将运算符栈中的元素移除到后缀式栈中直到遇到左括号
                    while (opStack.peek() != '(') {
                        postfixStack.push(String.valueOf(opStack.pop()));
                    }
                    opStack.pop();
                } else {
                    /**
                     *   如果是有左括号，加入运算符栈  \
                     *   如果当前运算符优先级低于先前  \
                     *   运算符, 先前运算符退出符号栈  \
                     *   进入后缀栈
                     */
                    while (currentOp != '(' && peekOp != ',' && compare(currentOp, peekOp)) {
                        postfixStack.push(String.valueOf(opStack.pop()));
                        peekOp = opStack.peek();
                    }
                    opStack.push(currentOp);
                }
                count = 0;
                currentIndex = i + 1;
            } else {
                count++;
            }
        }
        if (count > 1 || (count == 1 && !isOperator(arr[currentIndex]))) {// 最后一个字符不是括号或者其他运算符的则加入后缀式栈中
            postfixStack.push(new String(arr, currentIndex, count));
        }
        /**
         *   将操作符栈中的剩余的元素添加到后缀式栈中
         *   不包括 ， 符号
         */
        while (opStack.peek() != ',') {
            postfixStack.push(String.valueOf(opStack.pop()));
        }
    }

    /**
     * 判断是否为算术符号
     *
     * @param c
     * @return
     */
    private boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '(' || c == ')';
    }

    /**
     * 利用ASCII码-40做下标去算术符号优先级
     *
     * @param cur
     * @param peek
     * @return
     */
    public boolean compare(char cur, char peek) {// 如果是peek优先级高于cur，返回true，默认都是peek优先级要低
        boolean result = false;
        if (operatPriority[(peek) - 40] >= operatPriority[(cur) - 40]) {
            result = true;
        }
        return result;
    }

    /**
     * 按照给定的算术运算符做计算
     *
     * @param firstValue
     * @param secondValue
     * @param currentOp
     * @return
     */
    private String calculate(String firstValue, String secondValue, char currentOp) {
        String result = "";
        switch (currentOp) {
            case '+':
                result = String.valueOf(ArithHelper.add(firstValue, secondValue));
                break;
            case '-':
                result = String.valueOf(ArithHelper.sub(firstValue, secondValue));
                break;
            case '*':
                result = String.valueOf(ArithHelper.mul(firstValue, secondValue));
                break;
            case '/':
                result = String.valueOf(ArithHelper.div(firstValue, secondValue));
                break;
        }
        return result;
    }
}

class ArithHelper {

    // 默认除法运算精度
    private static final int DEF_DIV_SCALE = 16;

    // 这个类不能实例化
    private ArithHelper() {
    }

    /**
     * 提供精确的加法运算。
     *
     * @param v1 被加数
     * @param v2 加数
     * @return 两个参数的和
     */

    public static double add(String v1, String v2) {
        java.math.BigDecimal b1 = new java.math.BigDecimal(v1);
        java.math.BigDecimal b2 = new java.math.BigDecimal(v2);
        return b1.add(b2).doubleValue();
    }

    /**
     * 提供精确的减法运算。
     *
     * @param v1 被减数
     * @param v2 减数
     * @return 两个参数的差
     */

    public static double sub(String v1, String v2) {
        java.math.BigDecimal b1 = new java.math.BigDecimal(v1);
        java.math.BigDecimal b2 = new java.math.BigDecimal(v2);
        return b1.subtract(b2).doubleValue();
    }

    /**
     * 提供精确的乘法运算。
     *
     * @param v1
     *            被乘数
     * @param v2
     *            乘数
     * @return 两个参数的积
     */

    public static double mul(String v1, String v2) {
        java.math.BigDecimal b1 = new java.math.BigDecimal(v1);
        java.math.BigDecimal b2 = new java.math.BigDecimal(v2);
        return b1.multiply(b2).doubleValue();
    }

    /**
     * 提供（相对）精确的除法运算，当发生除不尽的情况时，精确到 小数点以后10位，以后的数字四舍五入。
     *
     * @param v1
     *            被除数
     * @param v2
     *            除数
     * @return 两个参数的商
     */

    public static double div(String v1, String v2) {
        java.math.BigDecimal b1 = new java.math.BigDecimal(v1);
        java.math.BigDecimal b2 = new java.math.BigDecimal(v2);
        return b1.divide(b2, DEF_DIV_SCALE, java.math.BigDecimal.ROUND_HALF_UP).doubleValue();
    }


    /**
     * 提供精确的小数位四舍五入处理。
     *
     * @param v 需要四舍五入的数字
     * @param scale 小数点后保留几位
     * @return 四舍五入后的结果
     */

    public static double round(String v, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The   scale   must   be   a   positive   integer   or   zero");
        }
        java.math.BigDecimal b = new java.math.BigDecimal(v);
        java.math.BigDecimal one = new java.math.BigDecimal("1");
        return b.divide(one, scale, java.math.BigDecimal.ROUND_HALF_UP).doubleValue();
    }
}

public class MainActivity extends AppCompatActivity {
    private Button[] bt;
    private TextView txt1;
    private TextView txt2;
    private String  signal;
    private int count=0;
    private int pointFlag;
    private int _pointFlag;

    public String ClassName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ClassName="BaseActivity";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Log.d("AlertMessage","Activity "+ClassName+"正在执行onCreate 被创建了  时间:"+formatter.format(new Date()));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始一系列参数
        bt = new Button[17];

        // 运算栈
        //staNumber = new Stack<String>();
        //staSignal = new Stack<String>();

        // 点运算符存在标志
        pointFlag = 0;
        _pointFlag = 0;

        // txt工作文本
        txt1 = (TextView) findViewById(R.id.text1);
        txt1.setText("");
        txt2 = (TextView) findViewById(R.id.text2);
        txt2.setText("");
        initial();
        Listeners();
        Log.d("AlertMessage","Activity "+ClassName+"完成执行onCreate 时间:"+formatter.format(new Date()));
    }
    @Override
    protected void onPause() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Log.d("AlertMessage","Activity "+ClassName+"正在执行onPause 看不到了 时间:"+formatter.format(new Date()));
        super.onPause();

        Log.d("AlertMessage","Activity "+ClassName+"完成执行onResume 时间:"+formatter.format(new Date()));
    }
    @Override
    protected void onStop() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Log.d("AlertMessage","Activity "+ClassName+"正在执行onStop 退出前台了  时间:"+formatter.format(new Date()));
        super.onStop();
        Log.d("AlertMessage","Activity "+ClassName+"完成执行onStop 时间:"+formatter.format(new Date()));
    }
    @Override
    protected void onStart() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Log.d("AlertMessage","Activity "+ClassName+"正在执行onStart 正在显示出来  时间:"+formatter.format(new Date()));
        super.onStart();
        Log.d("AlertMessage","Activity "+ClassName+"完成执行onStart 时间:"+formatter.format(new Date()));
    }
    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Log.d("AlertMessage","Activity "+ClassName+"正在执行onPostCreate 被销毁了  时间:"+formatter.format(new Date()));
        super.onPostCreate(savedInstanceState);
        Log.d("AlertMessage","Activity "+ClassName+"完成执行onPostCreate 时间:"+formatter.format(new Date()));
    }
    @Override
    protected void onDestroy() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Log.d("AlertMessage","Activity "+ClassName+"正在执行onDestroy 被销毁了  时间:"+formatter.format(new Date()));
        super.onDestroy();
        Log.d("AlertMessage","Activity "+ClassName+"完成执行onDestroy 时间:"+formatter.format(new Date()));
    }
    @Override
    protected void onPostResume() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Log.d("AlertMessage","Activity "+ClassName+"正在执行onPostResume 被销毁了  时间:"+formatter.format(new Date()));
        super.onPostResume();
        Log.d("AlertMessage","Activity "+ClassName+"完成执行onPostResume 时间:"+formatter.format(new Date()));
    }
    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Log.d("AlertMessage","Activity "+ClassName+"正在执行onTitleChanged 被销毁了  时间:"+formatter.format(new Date()));
        super.onTitleChanged(title, color);
        Log.d("AlertMessage","Activity "+ClassName+"完成执行onTitleChanged 时间:"+formatter.format(new Date()));
    }
    @Override
    protected void onNewIntent(Intent intent) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Log.d("AlertMessage","Activity "+ClassName+"正在执行onNewIntent正要已经出现  时间:"+formatter.format(new Date()));
        super.onNewIntent(intent);
        Log.d("AlertMessage","Activity "+ClassName+"完成执行onNewIntent 时间:"+formatter.format(new Date()));
    }
    @Override
    protected void onResume() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Log.d("AlertMessage","Activity "+ClassName+"正在执行onResume 正要已经出现  时间:"+formatter.format(new Date()));
        super.onResume();
        Log.d("AlertMessage","Activity "+ClassName+"完成执行onResume 时间:"+formatter.format(new Date()));
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Log.d("AlertMessage","Activity "+ClassName+"正在执行onSaveInstanceState 正要已经出现  时间:"+formatter.format(new Date()));
        super.onSaveInstanceState(outState);
        Log.d("AlertMessage","Activity "+ClassName+"完成执行onSaveInstanceState 时间:"+formatter.format(new Date()));
    }
    // 每个按钮设置事件及监听
    private void initial() {
        bt[0] = (Button) findViewById(R.id.button0); //0
        bt[1] = (Button) findViewById(R.id.button1); //1
        bt[2] = (Button) findViewById(R.id.button2); //2
        bt[3] = (Button) findViewById(R.id.button3); //3
        bt[4] = (Button) findViewById(R.id.button4); //4
        bt[5] = (Button) findViewById(R.id.button5); //5
        bt[6] = (Button) findViewById(R.id.button6); //6
        bt[7] = (Button) findViewById(R.id.button7); //7
        bt[8] = (Button) findViewById(R.id.button8); //8
        bt[9] = (Button) findViewById(R.id.button9); //9
        bt[10] = (Button) findViewById(R.id.button_add);    // +
        bt[11] = (Button) findViewById(R.id.button_nadd);   // -
        bt[12] = (Button) findViewById(R.id.button_mul);    // *
        bt[13] = (Button) findViewById(R.id.button_sub);    // /
        bt[14] = (Button) findViewById(R.id.button_out);    // .
        bt[15] = (Button) findViewById(R.id.button_clear);  // Clear
        bt[16] = (Button) findViewById(R.id.button16);      // =

        // 运算信号没有点击
        signal="#";
    }

    private void Listeners() {
        bt[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt1.setText(AddNumber(bt[0]));
            }
        });
        bt[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt1.setText(AddNumber(bt[1]));
            }
        });
        bt[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt1.setText(AddNumber(bt[2]));
            }
        });
        bt[3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt1.setText(AddNumber(bt[3]));
            }
        });
        bt[4].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt1.setText(AddNumber(bt[4]));
            }
        });
        bt[5].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt1.setText(AddNumber(bt[5]));
            }
        });
        bt[6].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt1.setText(AddNumber(bt[6]));
            }
        });
        bt[7].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt1.setText(AddNumber(bt[7]));
            }
        });
        bt[8].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt1.setText(AddNumber(bt[8]));
            }
        });
        bt[9].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt1.setText(AddNumber(bt[9]));
            }
        });
        bt[10].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txt1.getText().length() >= 0){
                    txt1.setText(AddSignal(bt[10]));
                }
            }
        });
        bt[11].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txt1.getText().length() >= 0){
                    txt1.setText(AddSignal(bt[11]));
                }
            }
        });
        bt[12].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txt1.getText().length() >= 0){
                    txt1.setText(AddSignal(bt[12]));
                }
            }
        });
        bt[13].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txt1.getText().length() >= 0){
                    txt1.setText(AddSignal(bt[13]));
                }
            }
        });
        bt[14].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txt1.getText().length() >= 1){
                    txt1.setText(AddNumber(bt[14]));
                }
            }
        });
        bt[15].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               txt1.setText("");
               txt2.setText("");
               signal="#";
               pointFlag=0;
               count = 0;
            }
        });
        bt[16].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(txt1.getText().toString()).contains("+")     &&
                        !(txt1.getText().toString()).contains("-") &&
                        !(txt1.getText().toString()).contains("*") &&
                        !(txt1.getText().toString()).contains("/") ){
                    // 空操作
                }
                else if(_pointFlag==1){
                    // 空操作
                }
                else if(signal!="#"){
                    // 空操作
                }
                else {
                    Work();
                    count=0;
                }
            }
        });
    }

    private void Work(){
        String expression;      String ans;
        expression = txt1.getText()+"";
        // 通过调用Calculator类的方法计算表达式
        double result = Calculator.conversion(expression);
        // 将数值转换为字符串
        ans= String.valueOf(result);
        txt2.setText(ans);
        txt1.setText("");
    }

    // 加数字
    private String AddNumber(Button source) {
        if((source.getText()+"").equals(".")){
            // 加入小数点,并将点信号设为真
            if(pointFlag == 0 && signal == "#" && count <= 7){
                // 当符号信号不为#时,加入点
                pointFlag = 1;
                _pointFlag = 1;
                return txt1.getText()+".";
            }
            else
                return txt1.getText()+"";
        }
        else if( (count > 1|| count==7) && signal != "#"){
            signal = "#";
            pointFlag = 0;
            count = 1;
            return txt1.getText() + "" + source.getText();
        }
        else if(count>7){
            return txt1.getText()+"";
        }
        else {
            signal = "#";
            count++;
            if(pointFlag == 1)
                _pointFlag=0;
            return txt1.getText()+""+source.getText();
        }
    }

    private String AddSignal(Button source) {
        // 当最后一个字符不为.时，允许添加运算符
        if (_pointFlag == 0) {
            // 运算符的添加
            if (signal != "#") {

                // 运算符变换
                int index = ("" + txt1.getText()).length();
                signal = source.getText() + "";
                return ("" + txt1.getText()).substring(0, index-1) + signal;
            } else if ((txt1.getText() + "").length() == 0 &&
                    (txt2.getText() + "").length() > 0) {

                // 支持间接连运算（两个文本框）
                String temp;
                // 获取符号
                signal = source.getText() + "";
                temp = (txt2.getText() + "") + signal;
                // 置空文本2
                txt2.setText("");
                return temp;
            } else {
                // 当文本1中为空时，添加运算符失败
                if ((txt1.getText() + "").length() == 0)
                    return "";
                signal = source.getText() + "";
                return txt1.getText() + "" + source.getText();
            }
        }
        else
            return txt1.getText()+"";
    }
}