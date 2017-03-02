#!/usr/bin/env python

from socket import *
import sys
import threading
import data_file
import time

def file_to_raw(input, output):
    output_file = open(output, 'wb')
    
    last_timestamp = 0
    f = open(input, "rb")
    packet_count = 0
    try:
        timestamp, data, length = data_file.read_packet(f)
        while data:
            output_file.write(data)
            packet_count += 1
            print "Packet %d saved. Size: %d [Wait %d]" % (packet_count, length, timestamp)
            timestamp, data, length = data_file.read_packet(f)
    except EOFError:
        print "Replication Done!"
    f.close()


input_filename = "record_160929\\streaming.dat"
output_filename = "test.raw"

file_to_raw(input_filename, output_filename)