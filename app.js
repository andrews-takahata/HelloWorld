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



app.get('/api/consulta-userr', function (req, res) {
   
    var sql = require("mssql");

    // config for your database
    var config = {
        user: 'root',
        password: 'senhaFiap',
        server: 'mysql', 
        database: 'castillo' 
    };

    // connect to your database
    sql.connect(config, function (err) {
    
        if (err) console.log(err);

        // create Request object
        var request = new sql.Request();
           
        // query to the database and get the records
        request.query('select * from user', function (err, recordset) {
            
            if (err) console.log(err)

            // send records as a response
            res.send(recordset);
            
        });
    });
});

module.exports = app