#include <stdio.h>
#include <time.h>
#include <stdint.h>
#include "Playcorder.h"
#include <stdlib.h>

FILE* fileHandle = NULL;
struct timeval lastPacketTime;

int gettimeofday(struct timeval * tp, struct timezone * tzp)
{
	// Note: some broken versions only have 8 trailing zero's, the correct epoch has 9 trailing zero's
	// This magic number is the number of 100 nanosecond intervals since January 1, 1601 (UTC)
	// until 00:00:00 January 1, 1970 
	static const uint64_t EPOCH = ((uint64_t)116444736000000000ULL);

	SYSTEMTIME  system_time;
	FILETIME    file_time;
	uint64_t    time;

	GetSystemTime(&system_time);
	SystemTimeToFileTime(&system_time, &file_time);
	time = ((uint64_t)file_time.dwLowDateTime);
	time += ((uint64_t)file_time.dwHighDateTime) << 32;

	tp->tv_sec = (long)((time - EPOCH) / 10000000L);
	tp->tv_usec = (long)(system_time.wMilliseconds * 1000);
	return 0;
}

BOOL Playcorder_Initialize_Write()
{
	// Get current time string
	time_t timeNow;
	char timeString[26];
	struct tm* timeInfo;
	time(&timeNow);
	timeInfo = localtime(&timeNow);
	strftime(timeString, 26, "%Y%m%d-%H%M%S.bin", timeInfo);

	fileHandle = fopen(timeString, "wb");
	gettimeofday(&lastPacketTime, NULL);

	return fileHandle != NULL;
}

BOOL Playcorder_Initialize_Read(char* filename, BOOL write)
{
	fileHandle = fopen(filename, "rb");

	return fileHandle != NULL;
}

void Playcorder_SavePacket(void* buffer, size_t size)
{
	struct timeval currentTime;
	gettimeofday(&currentTime, NULL);

	unsigned long long packetTimeMS = 1000 * (currentTime.tv_sec - lastPacketTime.tv_sec) + (currentTime.tv_usec - lastPacketTime.tv_usec) / 1000;
	lastPacketTime = currentTime;

	if (fileHandle == NULL)
		Playcorder_Initialize_Write();

	if (fileHandle == NULL)
		return;

	//unsigned int temp  = (data[0] << 0) | (data[1] << 8) | (data[2] << 16) | (data[3] << 24);

	long temp = _byteswap_ulong(packetTimeMS);
	fwrite((void*)&temp, 4, 1, fileHandle);
	temp = _byteswap_ulong(size);
	fwrite((void*)&temp, 4, 1, fileHandle);
	fwrite(buffer, size, 1, fileHandle);
}

void Playcorder_Play(PlaycorderDataReadyCallback callback)
{
	/*
            int packetTime = 0;
            int packetSize = 0;

            if (fileStream == null)
                fileStream = new FileStream(bufferFilename, FileMode.Open);

            // Read timestamp
            fileStream.Read(buffer, 0, 4);
            // Convert endianness (ARM is big endian, PC little)
            packetTime = (buffer[0] << 24) | (buffer[1] << 16) | (buffer[2] << 8) | buffer[3];

            // Read size
            fileStream.Read(buffer, 0, 4);
            // Convert endianness (ARM is big endian, PC little)
            packetSize = (buffer[0] << 24) | (buffer[1] << 16) | (buffer[2] << 8) | buffer[3];

            // Read packet
            fileStream.Read(buffer, 0, packetSize);

            // Wait for the packet time
            if (lastTime > 0)
                System.Threading.Thread.Sleep(packetTime - lastTime);
            lastTime = packetTime;

            return packetSize;
        }
		
    }*/
}