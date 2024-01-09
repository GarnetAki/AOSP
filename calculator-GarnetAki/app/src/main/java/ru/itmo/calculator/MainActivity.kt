package ru.itmo.calculator

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {

    private lateinit var input: EditText
    private lateinit var answer: EditText
    private lateinit var xVal: EditText
    private lateinit var yVal: EditText
    private lateinit var zVal: EditText
    private var openedParenthesis: Int = 0
    private var closedParenthesis: Int = 0
    private var focused: Char = 'i'

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        answer = findViewById(R.id.answerText)
        input = findViewById(R.id.displayText)
        xVal = findViewById(R.id.x_value)
        yVal = findViewById(R.id.y_value)
        zVal = findViewById(R.id.z_value)

        input.showSoftInputOnFocus = false
        xVal.showSoftInputOnFocus = false
        yVal.showSoftInputOnFocus = false
        zVal.showSoftInputOnFocus = false

        val tmp: Button = findViewById(R.id.input)
        tmp.background = getDrawable(R.drawable.main_button_selected_bg)
    }

    private fun isCommonOperation(chr: Char): Boolean {
        return chr == '+' || chr == '-' || chr == '÷' || chr == '×'
    }

    private fun isVariable(chr: Char): Boolean {
        return chr == 'X' || chr == 'Y' || chr == 'Z'
    }

    private fun removeAfterPointZeros(str: String): String {
        if (str == getString(R.string.empty_string) || str.isEmpty())
            return str

        var index = str.length
        var indexLast = 0
        var flag = 2
        while (str[index - 1] != '.'){
            if (index == 1) flag = 0
            if (!str[index - 1].isDigit()) flag = 0

            if (flag == 0)
                break

            if (flag == 2 && str[index - 1] != '0') {
                flag = 1
            }

            if (flag == 2)
                indexLast++

            index--
        }
        if (flag == 2)
            indexLast++

        var str1 = str
        if (flag > 0)
            str1 = str.dropLast(indexLast)

        return str1
    }

    private fun addParenthesis(str: String): String {
        var str1 = str
        for (i in 1 .. openedParenthesis - closedParenthesis)
            str1 = str1.plus(')')

        closedParenthesis = openedParenthesis
        return str1
    }

    private fun clearInput(){
        if (focused == 'i')
            setStringByFocus(getString(R.string.empty_input))
        else
            setStringByFocus(getString(R.string.zero))
    }

    private fun deleteInput(){
        if (getString(R.string.empty_input) == getStringByFocus().toString()){
            setStringByFocus(getString(R.string.empty_string))
        }

        var str = getStringByFocus().toString()

        if (str.isEmpty())
            throw Exception("Nothing to erase.")

        val chr = str.last()

        if (str.last() == '('){
            openedParenthesis--
            if (str.length > 1 && str[str.length - 2] == '√'){
                str = str.dropLast(1)
            }else if (str.length > 2 && str[str.length - 2] == 'n'){
                str = str.dropLast(2)
            }
        }

        if (str.last() == ')')
            closedParenthesis--

        str = str.dropLast(1)
        if (str.isNotEmpty() && !chr.isDigit())
            str = removeAfterPointZeros(str)

        if (str.isEmpty() || (str.length == 1 && str[0] == '-'))
            str = "0"

        setStringByFocus(str)
    }

    private fun getStringByFocus(): CharSequence? {
        return when(focused){
            'i' -> input.text
            'x' -> xVal.text
            'y' -> yVal.text
            'z' -> zVal.text
            else -> throw Exception("Something goes wrong (with focused).")
        }
    }

    private fun setStringByFocus(newStr: String) {
        when(focused){
            'i' -> {
                input.setText(newStr)
                input.setSelection(input.text.length)
            }
            'x' -> {
                xVal.setText(newStr)
                xVal.setSelection(xVal.text.length)
            }
            'y' -> {
                yVal.setText(newStr)
                yVal.setSelection(yVal.text.length)
            }
            'z' -> {
                zVal.setText(newStr)
                zVal.setSelection(zVal.text.length)
            }
            else -> throw Exception("Something goes wrong (with focused).")
        }
    }

    private fun insertNumber(newStr: String){

        if (getString(R.string.empty_input) == getStringByFocus().toString()){
            setStringByFocus(getString(R.string.empty_string))
        }

        var str = getStringByFocus().toString()
        if (str.isEmpty()){
            setStringByFocus(newStr)
        }else{
            if (str.last() == ')' || isVariable(str.last()))
                str = str.plus('×')
            else {
                if (str.last() == '0'){
                    if (str.length == 1 || (!str[str.length - 2].isDigit() && str[str.length - 2] != '.')){
                        str = str.dropLast(1)
                    }
                }

            }
            str = str.plus(newStr)
            setStringByFocus(str)
        }
    }

    private fun insertDefaultOperation(newStr: String){
        if (focused != 'i')
            throw Exception("Focused not on input. Confirm variables.")

        if (getString(R.string.empty_input) == input.text.toString()){
            input.text.clear()
        }

        var str = input.text.toString()
        if (str.isEmpty())
            throw Exception("Can not put a common operation in the beginning.")

        if (isCommonOperation(str.last()))
            str = str.dropLast(1)
        if (str.last() == '(')
            throw Exception("Can not put a common operation there.")

        if (str.last() == '0' || str.last() == '.')
            str = removeAfterPointZeros(str)

        str = str.plus(newStr)
        input.setText(str)
        input.setSelection(input.text.length)
    }

    private fun insertVariable(newStr: String){
        if (focused != 'i')
            throw Exception("Focused not on input. Confirm variables.")

        if (getString(R.string.empty_input) == input.text.toString()){
            input.text.clear()
        }

        var str = input.text.toString()
        if (str.isEmpty()){
            input.setText(newStr)
            input.setSelection(input.text.length)
        }else{
            if (str.last() == '0' || str.last() == '.')
                str = removeAfterPointZeros(str)

            if (str.last() == ')' || isVariable(str.last()) || str.last().isDigit())
                str = str.plus('×')

            str = str.plus(newStr)
            input.setText(str)
            input.setSelection(input.text.length)
        }
    }

    private fun insertParenthesisOperation(newStr: String){
        if (focused != 'i')
            throw Exception("Focused not on input. Confirm variables.")

        if (getString(R.string.empty_input) == input.text.toString()){
            input.text.clear()
        }

        var str = input.text.toString()
        if (str.isEmpty()){
            input.setText(newStr)
            openedParenthesis++
            input.setSelection(input.text.length)
        }else{
            if (str.last() == '0' || str.last() == '.')
                str = removeAfterPointZeros(str)

            if (str.last() == ')' || isVariable(str.last()) || str.last().isDigit())
                str = str.plus('×')

            openedParenthesis++
            str = str.plus(newStr)
            input.setText(str)
            input.setSelection(input.text.length)
        }
    }

    private fun insertPoint(){
        var str = getStringByFocus().toString()
        if (str.isEmpty()){
            setStringByFocus("0.")
        }else{
            var index = str.length
            while (str[index - 1] != '.'){
                if (index == 1) break
                if (!str[index - 1].isDigit()) break

                index--
            }
            if (str[index - 1] == '.')
                throw Exception("Number is actually pointed.")

            if (str.last() == ')' || isVariable(str.last()))
                str = str.plus('×')

            if (isCommonOperation(str.last())){
                str = str.plus("0.")
                setStringByFocus(str)
                return
            }

            str = str.plus('.')
            setStringByFocus(str)
        }
    }

    private fun changeSign(){
        if (getString(R.string.empty_input) == getStringByFocus().toString())
            throw Exception("There is no number to change it's sign.")

        var str = getStringByFocus().toString()
        if (str.isEmpty() || (!str.last().isDigit() && str.last() != '.'))
            throw Exception("There is no number to change it's sign.")

        var index = str.length
        while (index > 0 && (str[index - 1] == '.' || str[index - 1].isDigit())){
            index--
        }

        println(index)

        if (index == 0){
            str = '-'.plus(str)
            setStringByFocus(str)
            return
        }

        when (str[index - 1]){
            '+' -> {
                val l = str.dropLast(str.length - index + 1)
                val r = str.drop(index)
                str = (l.plus('-')).plus(r)
            }
            '-' -> {
                val l = str.dropLast(str.length - index + 1)
                val r = str.drop(index)
                str = if (index - 1 == 0 || !(str[index - 2].isDigit() || isVariable(str[index - 2]) || str[index - 2] == ')'))
                    l.plus(r)
                else (l.plus('+')).plus(r)
            }
            else -> {
                val l = str.dropLast(str.length - index)
                val r = str.drop(index)
                str = (l.plus('-')).plus(r)
            }
        }
        setStringByFocus(str)
    }

    private fun insertParenthesis(){
        if (focused != 'i')
            throw Exception("Focused not on input. Confirm variables.")

        if (getString(R.string.empty_input) == input.text.toString()){
            input.text.clear()
        }

        val str = input.text.toString()
        if (str.isEmpty()){
            input.setText("(")
            input.setSelection(input.text.length)
            openedParenthesis++
        }else{
            if (isCommonOperation(str.last()) || str.last() == '('){
                input.setText(str.plus('('))
                input.setSelection(input.text.length)
                openedParenthesis++
            }
            else{
                if (openedParenthesis <= closedParenthesis){
                    throw Exception("All parenthesis have a pair.")
                }else{
                    input.setText(str.plus(')'))
                    closedParenthesis++
                }
            }
        }
    }

    private fun calculate(){
        if (getString(R.string.empty_input) == input.text.toString() || input.text.toString().isEmpty())
            throw Exception("Nothing to calculate.")

        input.setText(addParenthesis(input.text.toString()))
        input.setSelection(input.text.length)
        var expression = input.text.toString()
        expression = expression.replace("X", xVal.text.toString())
        expression = expression.replace("Y", yVal.text.toString())
        expression = expression.replace("Z", zVal.text.toString())

        println(expression)
        val parseTree = ParseTree()
        parseTree.createTree(expression)
        print(parseTree.showTree())
        answer.setText(removeAfterPointZeros(parseTree.calculate().toString()))
    }

    fun focusChange(view: View){
        try {
            val button: Button = view as Button
            val buttonText: String = button.text.toString()
            when (focused){
                'x' -> {
                    xVal.setText(removeAfterPointZeros(xVal.text.toString()))

                    val tmp: Button = findViewById(R.id.input_x)
                    tmp.background = getDrawable(R.drawable.main_button_bg)
                }
                'y' -> {
                    yVal.setText(removeAfterPointZeros(yVal.text.toString()))

                    val tmp: Button = findViewById(R.id.input_y)
                    tmp.background = getDrawable(R.drawable.main_button_bg)
                }
                'z' -> {
                    zVal.setText(removeAfterPointZeros(zVal.text.toString()))
                    val tmp: Button = findViewById(R.id.input_z)
                    tmp.background = getDrawable(R.drawable.main_button_bg)
                }
                'i' -> {
                    input.setText(addParenthesis(removeAfterPointZeros(input.text.toString())))
                    val tmp: Button = findViewById(R.id.input)
                    tmp.background = getDrawable(R.drawable.main_button_bg)
                }
            }
            when (buttonText){
                "X:" -> {
                    focused = 'x'
                    val tmp: Button = findViewById(R.id.input_x)
                    tmp.background = getDrawable(R.drawable.main_button_selected_bg)
                }
                "Y:" -> {
                    focused = 'y'
                    val tmp: Button = findViewById(R.id.input_y)
                    tmp.background = getDrawable(R.drawable.main_button_selected_bg)
                }
                "Z:" -> {
                    focused = 'z'
                    val tmp: Button = findViewById(R.id.input_z)
                    tmp.background = getDrawable(R.drawable.main_button_selected_bg)
                }
                "Input:" -> {
                    focused = 'i'
                    val tmp: Button = findViewById(R.id.input)
                    tmp.background = getDrawable(R.drawable.main_button_selected_bg)
                }
            }
        }catch (e: Exception){
            answer.setTextColor(getColor(R.color.error))
            answer.setText(getString(R.string.error).plus(" ").plus(e.message))
        }
    }

    fun onClick(view: View) {
        val button: Button = view as Button
        val buttonText: String = button.text.toString()
        try {
            answer.setTextColor(getColor(R.color.black))
            answer.text.clear()
            when (buttonText) {
                "AC" -> clearInput()
                "." -> insertPoint()
                "=" -> calculate()
                "±" -> changeSign()
                "( )" -> insertParenthesis()
                "ln" -> insertParenthesisOperation("$buttonText(")
                "√" -> insertParenthesisOperation("$buttonText(")
                "⌫" -> deleteInput()
                "X" -> insertVariable(buttonText)
                "Y" -> insertVariable(buttonText)
                "Z" -> insertVariable(buttonText)
                "÷" -> insertDefaultOperation(buttonText)
                "×" -> insertDefaultOperation(buttonText)
                "-" -> insertDefaultOperation(buttonText)
                "+" -> insertDefaultOperation(buttonText)
                else -> insertNumber(buttonText)
            }
            if (buttonText != "=" && answer.text.isNotEmpty() && answer.text.first() != 'E'){
                val str = answer.text
                answer.setText(getString(R.string.old_res).plus(" ").plus(str))
            }
        }catch (e: Exception){
            answer.setTextColor(getColor(R.color.error))
            answer.setText(getString(R.string.error).plus(" ").plus(e.message))
        }
    }
}