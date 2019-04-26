#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Sat Feb  3 18:25:29 2018

@author: tophertighe
To change file, look at line 57.
"""

import re
import hashlib

#Opens the file and splits it into each individual word.
def openFile(filename):
    with open(filename, 'r') as file:
        text = []
        for line in file: 
            line = line.strip()
            word = re.split(', ', line)
            text = text + word
        return text

#Assuming the list of word from the file has had the header sentance stripped
#off, this will split the remaining elements into their own respective lists.
def formatPwrd(stuff):
    user = []
    salt = []
    pwrd = []
    for item in stuff:
        if stuff.index(item) % 3 is 0:
            user.append(item)
        elif stuff.index(item) % 3 is 1:
            salt.append(item)
        elif stuff.index(item) % 3 is 2:
            pwrd.append(item)
    return user, salt, pwrd

#For every word in the English language (or at least, in wordsEn.txt),
#add every salt code to it and compare it to the appropriate hash output.
#If any match, print the username and password
def dictAttack(user, salt, pwrd):
    with open("wordsEn.txt", 'r') as english:
        for word in english:
            word = word.strip() #Remove \n
            for i in range(len(pwrd)):
                sha = hashlib.sha1() #Do this in the loop to clear the buffer.
                entry = word + salt[i]
                entry = entry.encode()
                sha.update(entry)
                guess = sha.hexdigest()
                if guess == pwrd[i]:
                    print(user[i], " ", word)
    print("All done. Do I get a cookie now?")
            

#Prepare data structures
content = openFile("dictPwdFile39.txt")
del content[0] #These remove the header sentence.
del content[0]
user, salt, pwrd = formatPwrd(content)
#Attack!
dictAttack(user, salt, pwrd)
