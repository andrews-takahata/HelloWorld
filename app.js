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

module.exports = app