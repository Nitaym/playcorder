import socket
import sys
import select
import threading
import data_file
import time
from utils import current_milli_time


buffer_size = 1 * 1024 * 1024

def socket_to_file(port, filename):
    host = "0.0.0.0"
    s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    s.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    s.bind((host, port))

    f = open(filename, 'wb')
    try:
        data, addr = s.recvfrom(buffer_size)
        last_time = current_milli_time()
        while data:
#            print "Received Data"
            if len(data) >= buffer_size:
                print "Received Data of buffer size(!), increase buffer size"

            data_file.add_packet(f, current_milli_time() - last_time, data)
            last_time = current_milli_time()
            s.settimeout(None)
            data, addr = s.recvfrom(buffer_size)
    except socket.timeout:
        f.close()
        s.close()
        print "Socket Error"


streaming_port = 5000
flight_data_port = 5001

t1 = threading.Thread(target=socket_to_file, args=(streaming_port, "streaming.dat"))
t1.daemon = True
t1.start()
t2 = threading.Thread(target=socket_to_file, args=(flight_data_port, "flight_data.dat"))
t2.daemon = True
t2.start()

while t1.is_alive() or t2.is_alive():
    time.sleep(0.1)
