# This open-source program is distributed under the BSD license.
# (c) 2011 - Jouke Witteveen
# Based on an idea by Lennart Bal and a portage list by James Silcock.
# Revision 02

import time, math, e32, appuifw, messaging, positioning

phoneNumber = u"+316..."
interval = 1200
startAfter = 300
specials = u"5 19 29 46 54 59 62 63 69 71 73 76 78 80"

appuifw.app.title = u"DWhere"

latitudes = (
  51.36503, # Fixing the list indexing
  51.36503,
  51.36721,
  51.36937,
  51.36956,
  51.35910,
  51.35882,
  51.35840,
  51.35761,
  51.35748,
  51.35854,
  51.36052,
  51.36472,
  51.36600,
  51.36725,
  51.37546,
  51.38226,
  51.38750,
  51.39189,
  51.40249,
  51.40482,
  51.40736,
  51.41102,
  51.41403,
  51.41484,
  51.41686,
  51.41171,
  51.41046,
  51.40598,
  51.40210,
  51.40285,
  51.40080,
  51.40089,
  51.39640,
  51.39801,
  51.39938,
  51.40099,
  51.40146,
  51.40301,
  51.40224,
  51.39720,
  51.39327,
  51.39257,
  51.39363,
  51.39283,
  51.39329,
  51.39538,
  51.40014,
  51.40067,
  51.40747,
  51.41843,
  51.42462,
  51.43082,
  51.43189,
  51.43323,
  51.43598,
  51.43447,
  51.45087,
  51.45598,
  51.46000,
  51.47278,
  51.50158,
  51.52891,
  51.56037,
  51.55073,
  51.55201,
  51.56748,
  51.56139,
  51.53571,
  51.50985,
  51.49089,
  51.49107,
  51.46381,
  51.43861,
  51.41502,
  51.39119,
  51.38195,
  51.40515,
  51.40518,
  51.43094,
  51.50066
)
longitudes = (
  -2.2, # Fixing the list indexing and determining the course span
  -1.71642,
  -1.71171,
  -1.70083,
  -1.69717,
  -1.64438,
  -1.64054,
  -1.63598,
  -1.63203,
  -1.62830,
  -1.62498,
  -1.62229,
  -1.61573,
  -1.61383,
  -1.61150,
  -1.60182,
  -1.59446,
  -1.58688,
  -1.58379,
  -1.57112,
  -1.56862,
  -1.56564,
  -1.54832,
  -1.53945,
  -1.53252,
  -1.51815,
  -1.49622,
  -1.48014,
  -1.46771,
  -1.44708,
  -1.41175,
  -1.40207,
  -1.39279,
  -1.37017,
  -1.35800,
  -1.34764,
  -1.32792,
  -1.32554,
  -1.31241,
  -1.30132,
  -1.28529,
  -1.27146,
  -1.24825,
  -1.22659,
  -1.20984,
  -1.19404,
  -1.18032,
  -1.13782,
  -1.13058,
  -1.12447,
  -1.10070,
  -1.08501,
  -1.06904,
  -1.05822,
  -1.03257,
  -1.00472,
  -0.98671,
  -0.97391,
  -0.95532,
  -0.94299,
  -0.91833,
  -0.88335,
  -0.88551,
  -0.87384,
  -0.81071,
  -0.79447,
  -0.76918,
  -0.69543,
  -0.69872,
  -0.69047,
  -0.64158,
  -0.60441,
  -0.56908,
  -0.53874,
  -0.50105,
  -0.48616,
  -0.45917,
  -0.40685,
  -0.34649,
  -0.32299,
  -0.12045
)

def quit():
  app_timer.cancel()
  appuifw.app.set_exit()

def form_save(arg):
  global form_saved
  form_saved = True
  return True

def sleeep(interval):
  """Long sleep"""
  MAX_PERIOD = 1800
  # Timer is a signed 32bit integer storing microseconds, limit: 2147 seconds.
  while interval > 0:
    if interval > MAX_PERIOD:
      app_timer.after(MAX_PERIOD)
    else:
      app_timer.after(interval)
    interval -= MAX_PERIOD

def distance(origin, destination):
    lat1, lon1 = origin
    lat2, lon2 = destination
    radius = 6371 # km
    dlat = math.radians(lat2 - lat1)
    dlon = math.radians(lon2 - lon1)
    a = math.sin(dlat/2) * math.sin(dlat/2) + math.cos(math.radians(lat1)) \
        * math.cos(math.radians(lat2)) * math.sin(dlon/2) * math.sin(dlon/2)
    c = 2 * math.atan2(math.sqrt(a), math.sqrt(1-a))
    return radius * c

def firstge(lis, x):
  i = (len(lis) - 1) / 2
  if len(lis) <= 1:
    if i == 0 and lis[0] >= x:
      return 0
    else:
      return -1
  if lis[i] >= x:
    return firstge(lis[:i + 1], x)
  else:
    d = firstge(lis[i + 1:], x)
    if d < 0:
      return d
    else:
      return d + i + 1

positioning.select_module(positioning.default_module())
positioning.set_requestors([{"type":"service",
                             "format":"application",
                             "data":"dwhere"}])

app_timer = e32.Ao_timer()
appuifw.app.exit_key_handler = quit
form_saved = False

fields = [(u"Phone", "text", phoneNumber),
          (u"Interval", "number", interval),
          (u"1st wait", "number", startAfter),
          (u"Specials", "text", specials)]
app_form = appuifw.Form(fields, flags=appuifw.FFormEditModeOnly)
app_form.save_hook = form_save
app_form.execute()
if form_saved == True:
  phoneNumber = app_form[0][2]
  interval = app_form[1][2]
  startAfter = app_form[2][2]
  specials = map(int, app_form[3][2].split())
else:
  print "Please save the form data."
  quit(-1)

sleeep(startAfter)
pos = positioning.position()["position"]
lat = pos["latitude"]
lon = pos["longitude"]
while(longitudes[0] < lon <= longitudes[80]):
  message = time.strftime(u"%x %X, ")
  nextUp = firstge(longitudes, lon)
  if nextUp == 58:
    if lat <= latitudes[57]:
      nextUp = 57
  elif 60 < nextUp <= 63:
    if lat <= latitudes[61]:
      nextUp = 61
    elif lat <= latitudes[62]:
      nextUp = 62
    else:
      nextUp = 63
  elif nextUp == 64:
    if lat <= latitudes[62]:
      nextUp = 62
  elif 66 < nextUp <= 69:
    if lat >= latitudes[67]:
      nextUp = 67
    elif lat >= latitudes[68]:
      nextUp = 68
    else:
      nextUp = 69
  elif nextUp == 72:
    if lat < latitudes[72]:
      nextUp = 73
  elif nextUp > 78:
    if lat <= latitudes[79]:
      nextUp = 79
    else:
      nextUp = 80
  nextBig = specials[firstge(specials, nextUp)]
  message += u"portage %i at %.2f km" % (nextUp, distance((lat, lon), (latitudes[nextUp], longitudes[nextUp])))
  if nextUp != nextBig:
    message += u" (portage %i at %.2f km)" % (nextBig, distance((lat, lon), (latitudes[nextBig], longitudes[nextBig])))
  print "Sending:", message
  try:
    messaging.sms_send(phoneNumber, message)
  except:
    print "Could not send the SMS."
  sleeep(interval)
  pos = positioning.position()["position"]
  lat = pos["latitude"]
  lon = pos["longitude"]
else:
  print "Quit: You are outside the DW course."
