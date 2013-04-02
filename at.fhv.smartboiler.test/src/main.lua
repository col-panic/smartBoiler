local sched = require 'sched'
local serial = require 'serial'
local string = require 'string'

local function sms_callback(messageHeader, message)
	-- Implement desired action here
	print ('Msg Header:', messageHeader)
	print ('Msg: ', message)
end


local function initModem(modemPortWrite)
	modemPortWrite:write("ATZ0\r")
	modemPortWrite:write("AT+CNMI=1,1,0,0,0\r")
	modemPortWrite:write('AT+CPMS="SM"\r')
	modemPortWrite:flush()
end

local function readModem(modemPortRead, modemPortWrite, sms_callback)
	print("Listening for incoming modem data")
	counter = 0
	while true do
		line = modemPortRead:read("*l")
		print(counter .. ": ".. line)
		counter = counter+1
		
		-- SMS Delivery Indicator, e.g. "+CMTI: "SM",8
		mBegin, mEnd = string.find(line,'+CMTI: "SM",')
		if mEnd then
			messageIndex = string.sub(line,mEnd+1)
			modemPortWrite:write("AT+CMGR=" .. messageIndex .. "\r")
			modemPortWrite:flush()
		end
		
		-- SMS Message Indicator, e.g. "+CMGR: "REC UNREAD","+4369919790726",,"13/04/02,16:12:04+08"
		mBegin = string.find(line,'+CMGR: ') 
		if mBegin then
			sms_callback(line, modemPortRead:read("*l"))
		end
		
		-- RSSI Indicator
		mBegin = string.find(line,'RSSI: ')
		if(mBegin) then
			print("RSSI Change "+line)
		end
	end

end

local function main()
	local modemPortRead = io.open('/dev/ttyUSB2','r')
	local modemPortWrite = io.open('/dev/ttyUSB2','a')
	initModem(modemPortWrite)
	readModem(modemPortRead, modemPortWrite, sms_callback)
	--	modemPort:close()

end



sched.run(main)
-- Starting the sched main loop to execute the previous task
sched.loop()

