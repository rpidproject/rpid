# RPID Project

## Overview

It has become good data practice to use persistent identifiers (PIDs) to identify digital data products whether a product is a collection, a file, or an object of some type. Naming solutions for digital data products eventually resolve a PID down to the digital object it identifies, but **the current landscape is limited by multiple solutions with weak interoperability, and inconsistent protocols** for getting from PID to data object. In a world of increasing PID use, we will soon be awash with billions of PIDs that all resolve to data objects using various inconsistent and unpredictable approaches, making it difficult to build higher level services that cross the various approaches. 

In the initial 12+24 month project, **we stood up and supported a robust PID testbed** (called the RPID testbed (pronounced "rapid") to stimulate and enable evaluation of powerful new complementary outputs of the [Research Data Alliance](https://www.rd-alliance.org) (RDA) in PID oriented data management. The testbed is **responsive to data driven priorities in science and education**, specifically as part of the cyberinfrastructure ecosystem that accelerates a broad spectrum of data-intensive research (NSF Director unveils 2016). The advancements developed and tested here we believe have the **transformative magnitude to stimulate an entire ecosystem of new discovery services** for research data.

During the **Enhanced RPID project** which continued the work on the foundational services set up during the RPID project we will research **Repository Mapping Services** to allow the RPID service to be utilized without refactoring of data repositories and the **Digital Object Interface Protocol (DOIP) implementation** which allow direct operational interactions with Digital Objects. 

## Services
   * Handle Service - Development services can be seen at https://handle.grid.iu.edu:8000
   * Data Type Registry - Development services can be see at https://dtr.grid.iu.edu:8080
   * RDA PID Kernel Information WG profile
   * Repository Mapping Service - Prototype Q4 2019
   * RDA Collections WG API. 
   * RDA PIT API - enhanced to work with PID Kernel Information profile; available as a downloadable SDK. 

## Open Access for Data Science Researchers

### Usage Policies
   * The RPID testbed can be used for research, education, non-profit, or pre-competitive use
   * The RPID testbed assigns only test handles, allowing for exploratory work. 
   * Handles assigned by the testbed will be on a project basis, to avoid collisions. Your prefix will be 11723.9.test.\(proj_name)
   * Projects interested in using the testbed are strongly advised to join the RPID User Advisory Group
   * Follow the work at https://github.com/rpidproject.  
   * To join the RPID User Advisory Group or otherwise reach us, we are at rpid-l@iu.edu

### Requesting Access
   * Please contact the RPID working group at rpid-l@iu.edu to get started.   You will register your project, be assigned a prefix, and can then start using the services. 

### Communicate With the Project Team

Please feel free to contact the RPID Project Team at rpid-l@iu.edu 

## Additional Deliverables

  * The [RPID Puppet Repository](https://github.com/rpidproject/puppet) contains a suite of Puppet 4 manifests that can be used to standup an instance of the RPID Test Bed in an automated, repeatable fashion.

  * [Investigation of applicability of the PID architecture for CTS URNs](https://github.com/rpidproject/cts-handles/blob/master/proposal.md)

## Disclaimer

This material is based upon work supported by the National Science Foundation under Grant No. 1659310 and Grant No. 1839013 Any opinions, findings, and conclusions or recommendations expressed in this material are those of the author(s) and do not necessarily reflect the views of the National Science Foundation.
