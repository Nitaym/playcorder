#!/usr/bin/env python

from socket import *
import sys
import threading
import data_file
import time

def run(filename, new_fileame):
    fi = open(filename, "rb")
    fo = open(new_fileame, "wb")
    
    packet_count = 0
    try:
        last_time, data, length = data_file.read_packet(fi, True)
        timestamp = last_time
        while data:
            packet_count += 1
            data_file.add_packet(fo, timestamp - last_time, data)
            
            print "Packet %d" % (packet_count)
            last_time = timestamp
            timestamp, data, length = data_file.read_packet(fi, True)
            
    except EOFError:
        print "Replication Done!"

    fi.close()
    fo.close()


path = 'c:/Users/Nitay/Documents/Work/EdgyBees/DroneBox/Unity/DroneBoxTester/Assets/Recordings/GPS Bug/'
file_in = path + 'data-2017-02-27_18-29-05 Indoor Then GPS.raw'
file_out = path + 'data-2017-02-27_18-29-05 rel Indoor Then GPS.raw'

run(file_in, file_out)