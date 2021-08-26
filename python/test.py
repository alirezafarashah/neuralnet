import json
  
# Opening JSON file
f = open('network_weights.json',)
  
# returns JSON object as 
# a dictionary
data = json.load(f)
biases = data['biases']
weights = data['weights']

f1 = open('biases.txt', 'w')
f2 = open('weights.txt', 'w')

biases_string = ''
for x in biases:
    y = str(x)
    biases_string += y[1:len(y)-1] + '\n'
f1.write(biases_string)
f1.close()

weights_string = ''
for x in weights:
    for y in x:
        z = str(y)
        weights_string += z[1:len(z)-1] + '\n'
    weights_string += 'end\n'
f2.write(weights_string)
f2.close()