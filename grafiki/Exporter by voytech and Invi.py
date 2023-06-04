#!BPY

"""
Name: 'Invincible (.3df) anim votech'
Blender: 243
Group: 'Export'
Tooltip: 'Export to Invincible anim file format'
"""

__author__ = 'voytech'
__version__ = '1.0'
__email__ = "invinciblegame@gmail.com"
__bpydoc__ = """\
This script Exports a Invincible anim file format.
exportuje animacje
"""



import Blender
from Blender import *
 
filename = "f.a3df"

filename = filename
file = open(filename,"w")

ob = Object.GetSelected()[0]
me = Mesh.New()
#rrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr
liczbaVertex = 0
for klatka in [1]:
 Blender.Set('curframe', klatka)
 me.getFromObject(ob.name)
 for face in me.faces:
  numVertices = len(face.v)
  for vIndex in range( numVertices ):
   liczbaVertex=liczbaVertex+1
file.write(" %d\n" % liczbaVertex );
file.write(" %d\n" % 14 );
#rrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr

for klatka in [10]:
 Blender.Set('curframe', klatka)
 me.getFromObject(ob.name)
 for face in me.faces:
  numVertices = len(face.v)
  for vIndex in range( numVertices ):
   vertex = face.verts[vIndex]
   file.write("\t%f %f\n" % (face.uv[vIndex].x, face.uv[vIndex].y));
   file.write("\t%f %f %f\n" % (vertex.no.x, vertex.no.y, vertex.no.z ));
   file.write("\t%f %f %f\n" % (vertex.co.x, vertex.co.y, vertex.co.z ))
for klatka in [20, 30, 40, 50, 60, 70, 80]:
 Blender.Set('curframe', klatka)
 me.getFromObject(ob.name)
 for face in me.faces:
  numVertices = len(face.v)
  for vIndex in range( numVertices ):
   vertex = face.verts[vIndex]
   file.write("\t%f %f %f\n" % (vertex.no.x, vertex.no.y, vertex.no.z ));
   file.write("\t%f %f %f\n" % (vertex.co.x, vertex.co.y, vertex.co.z ))
file.close()