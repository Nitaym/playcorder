import struct


def add_packet(f, timestamp, packet):
    f.write(struct.pack("i", timestamp))
    f.write(struct.pack("i", len(packet)))
    f.write(packet)
    f.flush()


def read_packet(f):
    buf = f.read(4)
    if buf == '':
        raise EOFError
    timestamp = struct.unpack("i", buf)[0]
    buf = f.read(4)
    if buf == '':
        raise EOFError
    length = struct.unpack("i", buf)[0]
    packet = f.read(length)

    return timestamp, packet, length

