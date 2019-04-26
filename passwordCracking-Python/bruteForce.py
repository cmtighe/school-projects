#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Thu Feb 15 15:31:22 2018

@author: tophertighe
"""

import re
import hashlib
import datetime

#Opens the file and splits it into each individual word.
def openFile(filename):
    with open(filename, 'r') as file:
        text = []
        for line in file: 
            line = line.strip()
            word = re.split(', ', line)
            text = text + word
        return text
    
#Assuming the list of word from the file has had the header sentence stripped
#off, this will split the remaining elements into their own respective lists.
def formatPwrd(stuff):
    user = []
    pwrd = []
    for item in stuff:
        if stuff.index(item) % 2 is 0:
            user.append(item)
        else:
            pwrd.append(item)
    return user, pwrd

#Builds a list of all the possible characters
def gearUp():
    ammo = []
    for i in range(48, 58): #Numbers
        ammo.append(chr(i))
    for i in range(65, 91): #Uppercase
        ammo.append(chr(i))
    for i in range(97, 123): #Lowercase
        ammo.append(chr(i))
    ammo.append(chr(45)) #hyphen
    ammo.append(chr(95)) #underscore
    return ammo

#This exists because brute can only handle a set number of characters, and those
#characters must be passed to brute in a string.
def bruteWrap(ars, maxd):
    stamp = datetime.datetime.now()
    for i in range(0,maxd):
        word = [] #Create the string...
        for j in range(0,i+1):
            word.append("0") #...and fill it with 0's.
        brute(word, ars, 0, i, stamp) #Initial call
    print("All done. Do I get a cookie now?")

#Iterate the word, and check the hash against the password file.
#Iteration of the word is done recursively by checking in the character at
#depth is the last digit (ones place). If not, it will call brute again with 
#depth+1. Otherwise, if depth is the last digit, it will compute the has.
def brute(word, ars, depth, maxd, stamp): #start dpeth at 0
    for letter in ars:
        word[depth] = letter
        if depth < maxd:
            brute(word, ars, depth+1, maxd, stamp)
        #Compute hash
        entry = "".join(word)
        sha = hashlib.sha1()
        entry = entry.encode()
        sha.update(entry)
        guess = sha.hexdigest()
        #Check hash against password file
        for i in range(len(pwrd)):
            if guess == pwrd[i]:
                print(user[i], " ", entry.decode())
                print(datetime.datetime.now() - stamp, "\n")
      

content = openFile("randPwdFile39.txt")
del content[0] #Strip header sentence
user, pwrd = formatPwrd(content)
arsenal = gearUp()
bruteWrap(arsenal, 4) #Default set to 4, expected to complete in a few minutes.