#!/usr/bin/python
## Author: Karthikeya Udupa K M

import json
import math
import os
from array import *
import sys

## Functions for checking of the datatypes in the array.
def is_obj( data ):
    return (type(data) == list )

def is_bool( data ):
    return (type(data) == bool )

## Set true to see detailed output.
def is_debug():
	return False;

outputfilename = 'interruptibility.arff';
accelerometerdata = "";

if(len(sys.argv) < 2):
    print "Format: Python data_parser.py <Filename>"
    sys.exit(1)
    
with open (sys.argv[1], "r") as myfile:
    accelerometerdata=myfile.read().replace('\n','').replace("{", "[").replace("}","]").replace("(","[").replace(")","]").replace("yes","true").replace("no","false");

if accelerometerdata.endswith(","):
    accelerometerdata = accelerometerdata[:(len(accelerometerdata)-1)] + accelerometerdata[len(accelerometerdata):]

## Add brackets to fully convert the string to a valid JSON string.
accelerometerdata="["+accelerometerdata+"]";


## Open file to store the WEKA formatted output & write header information. 
f = open(outputfilename,'w')
f.write('@relation INTERRUPT\n');
f.write('@attribute meanAcc numeric\n');
f.write('@attribute varAcc numeric\n');
f.write('@attribute MCRAcc numeric\n');
f.write('@attribute interruptibility {\'yes\',\'no\'}\n');
f.write('\n@data\n');
f.close();

json_object = json.loads(accelerometerdata)
for current_sample_set in json_object:

	mean_variance_sum = 0.0;
	individual_mean_values = array('d',[])
	acc_reading_cnt = 0;
	current_introp = 'no';
	predicated_value = '';
	for element in current_sample_set:
	 	if is_obj(element):
		 	for acc_reading in element:
			 	acc_reading_cnt = acc_reading_cnt + 1;
			 	current_reading_total = 0.0;
				for single_direction_info in acc_reading:
					current_reading_total = current_reading_total+math.pow(single_direction_info,2);
				mean_variance_sum = mean_variance_sum + math.sqrt(current_reading_total);
				individual_mean_values.append(math.sqrt(current_reading_total));
		else:
			if is_bool(element):
				if element:
					current_introp = 'yes'
				else:
					current_introp = 'no';
			
			if(predicated_value.__len__() > 0):
				predicated_value = predicated_value + ", "+ str(element);
			else:
				predicated_value = str(element);
				
	mean_intensity_val = mean_variance_sum/acc_reading_cnt;

	if is_debug():
		print "PREDICTED: " + predicated_value;	
			
	intensity_variance_sum = 0.0;
	previous_mean_val = 0.0;
	mcr_val_sum = 0.0;
	is_first_val = True;
	pntr = 0;
	for n in individual_mean_values:
		val = n - mean_intensity_val;
		intensity_variance_sum = intensity_variance_sum + (val * val);
		if pntr > 0:
			if (val*(individual_mean_values[pntr-1] - mean_intensity_val)) < 0:
				mcr_val_sum = mcr_val_sum+1;
		pntr=pntr+1;    
		
		intensity_variance = ((intensity_variance_sum) * (1.0/acc_reading_cnt));
        mcr_val = (mcr_val_sum) * (1.0/(acc_reading_cnt-1));
        
        if is_debug():
    		print "CALCULATED " + str(mean_intensity_val) + ', ' + str(intensity_variance) + ', ' + str(mcr_val) + ', ' + current_introp;

## Open file to write data.
	f = open('interruptibility.arff','a')
	f.write('\n' + str(mean_intensity_val) + ', ' + str(intensity_variance) + ', ' + str(mcr_val) + ', ' + current_introp);
	f.close()

print "Output file saved to: " + str(os.getcwd()) + "/" + outputfilename;
