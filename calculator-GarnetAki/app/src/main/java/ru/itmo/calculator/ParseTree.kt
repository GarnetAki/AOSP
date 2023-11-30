package ru.itmo.calculator

import java.math.BigDecimal
import kotlin.math.ln
import kotlin.math.sqrt

class ParseTree {
    private class Node {
        private lateinit var leftNode: Node
        private lateinit var rightNode: Node
        private lateinit var value: String

        private fun simplifyParenthesis(str: String): String{
            var parenthesisSub = 0
            var parenthesisMin = 0
            var flagOpen = true
            for (i in str.indices){
                if (str[i] == ')'){
                    parenthesisSub--
                }else{
                    if (parenthesisSub < parenthesisMin)
                        parenthesisMin = parenthesisSub

                    if (str[i] == '(') {
                        parenthesisSub++
                        if (flagOpen)
                            parenthesisMin++
                    }else{
                        flagOpen = false
                    }
                }
            }
            return str.substring(parenthesisMin, str.length - parenthesisMin)
        }

        fun buildSubTree(expression: String){
            if (expression.isEmpty())
                throw Exception("There is no parameter for operation.")
            println(expression)

            val tmp = simplifyParenthesis(expression)
            if (expression.isEmpty())
                throw Exception("There is no parameter for operation.")

            var parenthesisSub = 0
            var index = -1
            for (i in tmp.indices){
                when (tmp[i]){
                    ')' -> parenthesisSub--
                    '(' -> parenthesisSub++
                    '×' -> {
                        if (parenthesisSub == 0)
                            index = i
                    }
                    '÷' -> {
                        if (parenthesisSub == 0)
                            index = i
                    }
                    '+' -> {
                        if (parenthesisSub == 0){
                            value = "+"
                            leftNode = Node()
                            leftNode.buildSubTree(tmp.substring(0, i))
                            rightNode = Node()
                            rightNode.buildSubTree(tmp.substring(i + 1, tmp.length))
                            return
                        }
                    }
                    '-' -> {
                        if (parenthesisSub == 0){
                            if (i > 0 && (tmp[i - 1].isDigit() || tmp[i - 1] == ')')){
                                leftNode = Node()
                                leftNode.buildSubTree(tmp.substring(0, i))
                                value = "-"
                                rightNode = Node()
                                rightNode.buildSubTree(tmp.substring(i + 1, tmp.length))
                                return
                            }else{
                                if (index == -1)
                                    index = i
                            }
                        }
                    }
                }
            }
            if (index != -1){
                value = tmp[index].toString()
                if (value != "-"){
                    leftNode = Node()
                    leftNode.buildSubTree(tmp.substring(0, index))
                }
                rightNode = Node()
                rightNode.buildSubTree(tmp.substring(index + 1, tmp.length))
                return
            }
            if (tmp[0] == 'l'){
                value = "ln"
                rightNode = Node()
                rightNode.buildSubTree(tmp.substring(3, tmp.length - 1))
                return
            }
            if (tmp[0] == '√'){
                value = "√"
                rightNode = Node()
                rightNode.buildSubTree(tmp.substring(2, tmp.length - 1))
                return
            }
            value = tmp
        }

        fun calculateNode(): BigDecimal{
            if (!this::value.isInitialized)
                throw Exception("There is no parameter for operation.")

            when (value){
                "ln" -> {
                    if (!this::rightNode.isInitialized)
                        throw Exception("There is no parameter for operation.")

                    val tmp = rightNode.calculateNode()
                    if (tmp <= 0.toBigDecimal())
                        throw Exception("Somewhere logarithm of a non-positive number.")

                    return ln(tmp.toDouble()).toBigDecimal()
                }
                "√" -> {
                    if (!this::rightNode.isInitialized)
                        throw Exception("There is no parameter for operation.")

                    val tmp = rightNode.calculateNode()
                    if (tmp < 0.toBigDecimal())
                        throw Exception("Somewhere sqrt of negative number.")

                    return sqrt(tmp.toDouble()).toBigDecimal()
                }
                "+" -> {
                    if (!this::rightNode.isInitialized || !this::leftNode.isInitialized)
                        throw Exception("There is no parameter for operation.")

                    return leftNode.calculateNode() + rightNode.calculateNode()
                }
                "-" -> {
                    if (!this::rightNode.isInitialized)
                        throw Exception("There is no parameter for operation.")

                    if (!this::leftNode.isInitialized)
                        return (-1 * rightNode.calculateNode().toDouble()).toBigDecimal()

                    return leftNode.calculateNode() - rightNode.calculateNode()
                }
                "×" -> {
                    if (!this::rightNode.isInitialized || !this::leftNode.isInitialized)
                        throw Exception("There is no parameter for operation.")

                    return leftNode.calculateNode() * rightNode.calculateNode()
                }
                "÷" -> {
                    if (!this::rightNode.isInitialized || !this::leftNode.isInitialized)
                        throw Exception("There is no parameter for operation.")

                    val tmp = rightNode.calculateNode()
                    if (tmp == 0.toBigDecimal())
                        throw Exception("Somewhere dividing on 0.")

                    return leftNode.calculateNode() / tmp
                }
                else -> {
                    return value.toBigDecimal()
                }
            }
        }

        fun showNode(str: String, tab: Int): String{
            if (!this::value.isInitialized)
                throw Exception("There is no parameter for operation.")

            var tmp = str
            for (i in 1 .. tab)
                tmp = tmp.plus("  ")

            tmp = tmp.plus("] ".plus(value)).plus("\r\n")
            if (this::leftNode.isInitialized)
                tmp = leftNode.showNode(tmp, tab + 1)
            if (this::rightNode.isInitialized)
                tmp = rightNode.showNode(tmp, tab + 1)
            return tmp
        }
    }

    private lateinit var head: Node

    fun createTree(expression: String){
        head = Node()
        head.buildSubTree(expression)
    }

    fun showTree(): String{
        return head.showNode("", 0)
    }

    fun calculate(): BigDecimal{
        if (!this::head.isInitialized)
            throw Exception("Tree was not init yet.")

        return head.calculateNode()
    }
}