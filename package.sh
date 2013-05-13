#!/bin/bash

# build and packge our hacked up server thing...
# see docs/hacking.txt for more info on how this works

wget http://www.ark.cs.cmu.edu/TweetNLP/model.20120919
mv model.20120919 ark-tweet-nlp/src/main/resources/cmu/arktweetnlp/
mvn package
mv ark-tweet-nlp/target/bin/ark-tweet-nlp-0.3.2.jar ark-tweet-nlp-server-0.3.2.jar

# then just run the server with:
# java -jar ark-tweet-nlp-server-0.3.2.jar
