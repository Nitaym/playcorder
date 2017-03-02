/*************************************************************************
 *
 * EDGYBEES PROPRIETARY
 * __________________
 *
 *  [2016] - [2017] EdgyBees Limited
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of EdgyBees Limited and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to EdgyBees Limited
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from EdgyBees Limited
 */

using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Runtime.InteropServices;
using System.Text;

namespace DroneBox
{
    public class SmartBuffer
    {
        byte[] buffer;
        IntPtr bufferUnmanaged;
        int lastTime = -1;

        FileStream fileStream = null;

        string bufferFilename = "";

        public SmartBuffer(string filename)
        {
            int bufferSize = 100 * 1024;
            buffer = new byte[bufferSize];
            bufferUnmanaged = Marshal.AllocHGlobal(bufferSize);

            bufferFilename = filename;
        }

        ~SmartBuffer()
        {
            Marshal.FreeHGlobal(bufferUnmanaged);
        }

        private void ResetStream()
        {
//             fileStream.Close();
//             fileStream = null;
            fileStream.Seek(0, SeekOrigin.Begin);
            lastTime = 0;
        }

        private int ReadPacket()
        {
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

        public IntPtr GetFrame(out int packetSize)
        {
            try
            {
                packetSize = ReadPacket();
            }
            catch (Exception)
            {
                ResetStream();
                packetSize = ReadPacket();
            }

            Marshal.Copy(buffer, 0, bufferUnmanaged, packetSize);

            return bufferUnmanaged;
        }

        public string GetString()
        {
            int packetSize;
            try
            {
                packetSize = ReadPacket();
            }
            catch (Exception)
            {
                ResetStream();
                packetSize = ReadPacket();
            }
            string json = ASCIIEncoding.ASCII.GetString(buffer, 0, packetSize);
            return json;
        }
    }
}