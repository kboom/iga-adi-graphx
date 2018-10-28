package edu.agh.kboom

import edu.agh.kboom.production.Production

sealed case class IgaMessage(production: Production)
