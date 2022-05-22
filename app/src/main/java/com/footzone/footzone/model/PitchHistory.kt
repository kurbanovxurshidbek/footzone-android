package com.footzone.footzone.model

data class PitchHistory(val name: String, val date: String, val hour: Hour, val price: Int) {

}

data class Hour(val start: String, val end: String)
