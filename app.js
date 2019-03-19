'use strict'

const express = require('express');
const app = express();
const moment = require('moment');

app.get('/api', function (req, res) {
    res.send('hello world');
});

app.get('/api/today', function (req, res) {
    let date = moment(new Date()).locale('pt-br').format('ddd, D [de] MMMM [de] YYYY');
    res.send(date);
});



app.get('/api/consulta-user', function (req, res) {
   
    var mysql = require('mysql');
	
	var sql = "select * from user";

    // config for your database
    var con = mysql.createConnection({
        user: 'root',
        password: 'senhaFiap',
        host: 'mysql', 
        database: 'castillo' 
    });

    // connect to your database
   con.connect(function(err) {
	   if (err) throw err;
	   console.log("Connected");
	   con.query(sql, function (err, result){
		   if (err) throw err;
		   console.log("Result: " + result);
	   });
	 
   });  
});

module.exports = app