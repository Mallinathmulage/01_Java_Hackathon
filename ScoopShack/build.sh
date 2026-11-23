#!/bin/sh
# Compile and run ScoopShack (needs JDK 17+)
mkdir -p out
javac -d out $(find src -name "*.java") && java -cp out com.scoopshack.app.ScoopShackApp
