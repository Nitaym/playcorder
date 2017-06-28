import struct
import time

def endianess_fix(x, reverse_endianness):
    if reverse_endianness:
        return (((x << 24) & 0xFF000000) |
                ((x <<  8) & 0x00FF0000) |
                ((x >>  8) & 0x0000FF00) |
                ((x >> 24) & 0x000000FF))
    else:
        return x

            
def current_milli_time():
    return int(round(time.time() * 1000))

            
def add_packet(f, timestamp, packet, reverse_endianness=False):
    buf = endianess_fix(timestamp, reverse_endianness)
    f.write(struct.pack("I", buf))
    buf = endianess_fix(len(packet), reverse_endianness)
    f.write(struct.pack("I", buf))
    f.write(packet)
    f.flush()


def read_packet(f, reverse_endianness=False):
    buf = f.read(4)
    if buf == '':
        raise EOFError
    timestamp = endianess_fix(struct.unpack("I", buf)[0], reverse_endianness)

    buf = f.read(4)
    if buf == '':
        raise EOFError
    length = endianess_fix(struct.unpack("I", buf)[0], reverse_endianness)
    packet = f.read(length)

    return timestamp, packet, length

