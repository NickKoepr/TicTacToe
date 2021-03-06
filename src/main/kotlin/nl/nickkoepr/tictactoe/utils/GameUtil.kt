package nl.nickkoepr.tictactoe.utils

object GameUtil {

    //Return the request choice based on the id of the button.
    fun buttonToRequestChoice(id: String): Boolean{
        return when (id){
            "accept" -> true
            "decline" -> false
            else -> false
        }
    }

    //Check if the button is a request button by looking at the id of the button.
    fun isRequestButton(id: String): Boolean{
        return when(id){
            "accept" -> true
            "decline" -> true
            else -> false
        }
    }
}