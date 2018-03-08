#!/usr/bin/env python

from socket import *
import sys
import threading
import data_file
import time

reverse_endianess = True

def print_file(filename):
    last_timestamp = 0
    f = open(filename, "rb")
    packet_count = 0
    try:
        timestamp, data, length = data_file.read_packet(f, reverse_endianess)
        while data:
            wait_time = timestamp -last_timestamp
            print("Waiting for %d" % wait_time)
            time.sleep(wait_time / 1000.0)
            last_timestamp = timestamp
            
            packet_count += 1
            print("Packet %d. Size: %d [Wait %d]: %s" % (packet_count, length, wait_time, data))
            timestamp, data, length = data_file.read_packet(f, reverse_endianess)
    except EOFError:
        print("Replication Done!")
    f.close()

    
#filename = r'c:\Users\Nitay\Documents\Work\EdgyBees\DroneBox\Unity\DroneBoxTester\Assets\Recordings\GPS Bug\data-2017-03-05_09-40-13[1] - Bad.raw'
#filename = r'c:\Users\Nitay\Documents\Work\EdgyBees\DroneBox\Unity\DroneBoxTester\Assets\Recordings\GPS Bug\data-2017-03-05_11-03-22[1] - OK.raw'
filename = r'f:\Users\Nitay\Dropbox\Work\Playcorder\C\VSProject\PlaycorderTester\20180308-145554.bin'
print_file(filename)

