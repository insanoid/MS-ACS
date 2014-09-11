#!/usr/bin/python

from mininet.net import Mininet
from mininet.util import createLink

net = Mininet()

c0 = net.addController()
h0 = net.addHost('h0')
h1 = net.addHost('h1')
s0 = net.addSwitch('s0')


net.addLink(h0, s0)
net.addLink(h1, s0)

h0.setIP(<ip>,port)
h1.setIP(<ip>,port)

net.start()
net.pingAll()
net.stop()