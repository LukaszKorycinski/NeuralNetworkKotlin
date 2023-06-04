#!BPY

"""
Name: 'Invincible (.3df) java bones'
Blender: 243
Group: 'Export'
Tooltip: 'Export to Invincible bones file format for java'
"""

__author__ = 'Invi'
__version__ = '0.8'
__email__ = "lukasz.korycinski@gmail.com"
__bpydoc__ = """\
This script Exports a Invincible anim file format.
exportuje animacje
"""

import Blender
from Blender import Mesh 
from Blender import Armature
filename = "f.a3df"

filename = filename
file = open(filename,"w")


scene = Blender.Scene.GetCurrent()

meshList = [ob for ob in scene.objects if ob.type == 'Mesh']


liczbaVertex = 0

for object in meshList:
 mesh = object.getData(0,1)
	
 faceCount = 0
 lastShadeType = -1
 lastNumVertices = -1
	
 for face in mesh.faces:
  numVertices = len(face.v)
  if lastNumVertices == -1 or lastNumVertices != numVertices:
   lastNumVertices = numVertices 
  for vIndex in range( numVertices ):
   vertex = face.verts[vIndex]
   liczbaVertex=liczbaVertex+1
   mesz=mesh

file.write(" %d " % liczbaVertex );

for numAnim in [1]:
 for object in meshList:
   Blender.Set("curframe", numAnim)
   mesh.getFromObject(object.name)

	
   faceCount = 0
   lastShadeType = -1
   lastNumVertices = -1
	
   for face in mesh.faces:
    numVertices = len(face.v)
    if lastNumVertices == -1 or lastNumVertices != numVertices:
     lastNumVertices = numVertices 
    for vIndex in range( numVertices ):
     vertex = face.verts[vIndex]
     file.write(" %f %f " % (face.uv[vIndex].x, face.uv[vIndex].y));
     file.write(" %f %f %f " % (vertex.no.x, vertex.no.y, vertex.no.z ));
     file.write(" %f %f %f " % (vertex.co.x, vertex.co.y, vertex.co.z ))
    faceCount = faceCount + 1
    mesh = mesz


file.close()