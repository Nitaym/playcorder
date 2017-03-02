#!/usr/bin/env python

from socket import *
import sys
import threading
import data_file
import time

def run(cut_time, filename, new_fileame):
    fi = open(filename, "rb")
    fo = open(new_fileame, "wb")
    
    packet_count = 0
    try:
        timestamp, data, length = data_file.read_packet(fi, True)
        while data:
            packet_count += 1
            if (timestamp > cut_time):
                data_file.add_packet(fo, timestamp - cut_time, data, True)
            
            print "Packet %d" % (packet_count)
            timestamp, data, length = data_file.read_packet(fi, True)
            
    except EOFError:
        print "Replication Done!"

    fi.close()
    fo.close()


path = 'c:/Users/Nitay/Documents/Work/EdgyBees/DroneBox/Unity/DroneBoxTester/Assets/Recordings/GPS Bug/'
file_in = path + 'data-2017-02-27_18-29-05 Indoor Then GPS.raw'
file_out = path + 'Indoor Then GPS.raw'

run(115000, file_in, file_out)