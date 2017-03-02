#!/usr/bin/env python

from socket import *
import sys
import threading
import data_file
import time

def file_to_socket(port, filename):
    s = socket(AF_INET, SOCK_DGRAM)
    #host = "127.0.0.1" #sys.argv[1]
    host = "192.168.1.209" #sys.argv[1]
    #host = "192.168.1.173" #sys.argv[1]
    #host = "10.0.0.14" #sys.argv[1]

    addr = (host, port)

    last_timestamp = 0
    f = open(filename, "rb")
    packet_count = 0
    try:
        timestamp, data, length = data_file.read_packet(f)
        while data:
            time.sleep(timestamp / 1000.0)
                      
            # print " ".join(hex(ord(n)) for n in data[:20])

            # if data[1] == '\x60':
                # data = data[0] + '\xe0' + data[2:]
            # if data[12:16] == '\x00\x00\x00\x01':
                # data = data[:12] + data[16:]
            # data = data[12:]
                
            # data = data[16:]
                
            # print " ".join(hex(ord(n)) for n in data[:20])
            # exit()
                
            
            if s.sendto(data, addr):
                packet_count += 1
                print "Packet %d Sent. Size: %d [Wait %d]" % (packet_count, length, timestamp)
                timestamp, data, length = data_file.read_packet(f)
    except EOFError:
        print "Replication Done!"
    s.close()
    f.close()

streaming_port = 5000
flight_data_port = 8089


streaming_filename = "record160929\\streaming.dat"
t1 = threading.Thread(target=file_to_socket, args=(streaming_port, streaming_filename))
t1.daemon = True
t1.start()
#flightdata_filename = "record160929\\flight_data.dat"
#t2 = threading.Thread(target=file_to_socket, args=(flight_data_port, flightdata_filename))
#t2.daemon = True
#t2.start()

while t1.is_alive(): #or t2.is_alive():
    time.sleep(0.1)
