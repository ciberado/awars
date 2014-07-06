function buildDataVizGeometries(conflictReport) {
    console.info('Building data for {0} conflicts.'.replace('{0}', conflictReport.length));
    var loadLayer = document.getElementById('loading');
    loadLayer.style.display = 'block';

    for (var i = 0; i < conflictReport.length; i++) {
        var conflict = conflictReport[i];
        var exporterName = conflict.attacker.toUpperCase();
        var importerName = conflict.defender.toUpperCase();
        var exporterCountry = countryData[exporterName];
        var importerCountry = countryData[importerName];
        //	we couldn't find the country, it wasn't in our list...
        if (exporterCountry === undefined || importerCountry === undefined) {
            console.warn('Undefined country {0} or {1}.'
                    .replace('{0}', exporterName)
                    .replace('{1}', importerName));
            continue;
        }
        conflict.attackerLineGeometry = makeConnectionLineGeometry(exporterCountry, importerCountry, 0.6);
        conflict.defenderLineGeometry = makeConnectionLineGeometry(importerCountry, exporterCountry, 0.4);
    }

    loadLayer.style.display = 'none';
}

function getVisualizedMesh(conflictReport) {

    // Man, this is really ugly. TODO: refactor the whole thing.
    var conflictPages = [];
    for (var i = 0; i < conflictReport.length; i++) {
        var attackerPage = {
            color: new THREE.Color(exportColor),
            lineGeometry: conflictReport[i].attackerLineGeometry,
            particleCount: 5,
            particleSize: 30,
            particleVelocity: 0.5
        };
        var defenderPage = {
            color: new THREE.Color(importColor),
            lineGeometry: conflictReport[i].defenderLineGeometry,
            particleCount: conflictReport[i].machinesCount,
            // 6 tiny: 0.25 * 6 * 150 -> 225
            particleSize: conflictReport[i].machineWeight * conflictReport[i].machinesCount * 100,
            particleVelocity: conflictReport[i].requestPerSecond / 80
        };
        conflictPages.push(attackerPage);
        conflictPages.push(defenderPage);
    }

    var linesGeo = new THREE.Geometry();
    var lineColors = [];

    var particlesGeo = new THREE.Geometry();
    var particleColors = [];

    for (var i = 0; i < conflictPages.length; i++) {
        var conflictPage = conflictPages[i];

        var lineColor = conflictPage.color;
        var lineGeometry = conflictPage.lineGeometry;

        if (lineGeometry == undefined) {
            console.warn('Undefined geometry.', conflictPage);
            continue;
        }

        var lastColor;
        //	grab the colors from the vertices
        for (s in lineGeometry.vertices) {
            var v = lineGeometry.vertices[s];
            lineColors.push(lineColor);
            lastColor = lineColor;
        }

        //	merge it all together
        THREE.GeometryUtils.merge(linesGeo, lineGeometry);

        var particleColor = lastColor.clone();		// DOC: Color de la partícula
        var points = lineGeometry.vertices;
        var particleCount = conflictPage.particleCount;
        particleCount = constrain(parseInt(particleCount), 1, 100);
        var particleSize = constrain(conflictPage.particleSize, 25, 100);
        for (var s = 0; s < particleCount; s++) {
            var desiredIndex = s / particleCount * points.length;
            var rIndex = constrain(Math.floor(desiredIndex), 0, points.length - 1);

            var point = points[rIndex];
            var particle = point.clone();
            particle.moveIndex = rIndex;
            particle.nextIndex = rIndex + 1;
            if (particle.nextIndex >= points.length)
                particle.nextIndex = 0;
            particle.lerpN = 0;
            particle.velocity = conflictPage.particleVelocity;
            particle.path = points;
            particlesGeo.vertices.push(particle);
            particle.size = particleSize;
            particleColors.push(particleColor);
        }

    }

    linesGeo.colors = lineColors;

    //	make a final mesh out of this composite
    var splineOutline = new THREE.Line(linesGeo, new THREE.LineBasicMaterial(
            {color: 0xffffff, opacity: 1.0, blending:
                        THREE.AdditiveBlending, transparent: true,
                depthWrite: false, vertexColors: true,
                linewidth: 2})
            );

    splineOutline.renderDepth = false;


    attributes = {
        size: {type: 'f', value: []},
        customColor: {type: 'c', value: []}
    };

    uniforms = {
        amplitude: {type: "f", value: 1.0},
        color: {type: "c", value: new THREE.Color(0xffffff)},
        texture: {type: "t", value: 0, texture: THREE.ImageUtils.loadTexture("images/particleA.png")},
    };

    var shaderMaterial = new THREE.ShaderMaterial({
        uniforms: uniforms,
        attributes: attributes,
        vertexShader: document.getElementById('vertexshader').textContent,
        fragmentShader: document.getElementById('fragmentshader').textContent,
        blending: THREE.AdditiveBlending,
        depthTest: true,
        depthWrite: false,
        transparent: true,
        // sizeAttenuation: true,
    });



    var particleGraphic = THREE.ImageUtils.loadTexture("images/map_mask.png");
    var particleMat = new THREE.ParticleBasicMaterial({map: particleGraphic, color: 0xffffff, size: 60,
        blending: THREE.NormalBlending, transparent: true,
        depthWrite: false, vertexColors: true,
        sizeAttenuation: true});
    particlesGeo.colors = particleColors;
    var particleSystem = new THREE.ParticleSystem(particlesGeo, shaderMaterial);
    particleSystem.dynamic = true;
    splineOutline.add(particleSystem);

    var vertices = particleSystem.geometry.vertices;
    var values_size = attributes.size.value;
    var values_color = attributes.customColor.value;

    for (var v = 0; v < vertices.length; v++) {
        values_size[ v ] = particleSystem.geometry.vertices[v].size;
        values_color[ v ] = particleColors[v];
    }

    particleSystem.update = function() {
        // var time = Date.now()									
        for (var i in this.geometry.vertices) {
            var particle = this.geometry.vertices[i];
            var path = particle.path;
            var moveLength = path.length;

            particle.lerpN += particle.velocity; // DOC: velocidad de la partícula
            if (particle.lerpN > 1) {
                var step = Math.round(particle.lerpN);
                particle.moveIndex = particle.nextIndex + step;
                particle.nextIndex = particle.nextIndex + step + 1 ;
                if (particle.nextIndex >= path.length) {
                    particle.moveIndex = 0;
                    particle.nextIndex = 1;
                }
                particle.lerpN = 0;
            }

            var currentPoint = path[particle.moveIndex];
            var nextPoint = path[particle.nextIndex];


            particle.copy(currentPoint);
            particle.lerpSelf(nextPoint, particle.lerpN);
        }
        this.geometry.verticesNeedUpdate = true;
    };

    var affectedCountries = [];
    for (var i = 0; i < conflictReport.length; i++) {
        var exporterName = conflictReport[i].attacker.toUpperCase();
        if ($.inArray(exporterName, affectedCountries) < 0) {
            affectedCountries.push(exporterName);
        }

        var importerName = conflictReport[i].defender.toUpperCase();
        if ($.inArray(importerName, affectedCountries) < 0) {
            affectedCountries.push(importerName);
        }
    }

    //	return this info as part of the mesh package, we'll use this in selectvisualization
    splineOutline.affectedCountries = affectedCountries;


    return splineOutline;
}

function selectVisualization(conflictReport, selectedCountryName) {
    var cName = selectedCountryName ? selectedCountryName.toUpperCase() : 'SPAIN';

    $("#hudButtons .countryTextInput").val(cName);
    previouslySelectedCountry = selectedCountry;
    selectedCountry = countryData[cName];


    //	clear off the country's internally held color data we used from last highlight
    for (var i in countryData) {
        var country = countryData[i];
        country.exportedAmount = 0;
        country.importedAmount = 0;
        country.mapColor = 0;
    }

    //	clear markers
    /*
     for( var i in selectableCountries ){
     removeMarkerFromCountry( selectableCountries[i] );
     }
     */
    //	clear children
    while (visualizationMesh.children.length > 0) {
        var c = visualizationMesh.children[0];
        visualizationMesh.remove(c);
    }

    //	build the mesh
    console.time('getVisualizedMesh');
    var mesh = getVisualizedMesh(conflictReport);
    console.timeEnd('getVisualizedMesh');

    //	add it to scene graph
    visualizationMesh.add(mesh);


    //	alright we got no data but at least highlight the country we've selected
    if (mesh.affectedCountries.length == 0) {
        mesh.affectedCountries.push(cName);
    }

    for (var i in mesh.affectedCountries) {
        var countryName = mesh.affectedCountries[i];
        var country = countryData[countryName];
        if (country == undefined) {
            console.warn('Undefined country.', country);
            continue;
        }
        attachMarkerToCountry(countryName, country.mapColor);
    }

    // console.log( mesh.affectedCountries );
    highlightCountry(mesh.affectedCountries);

    if (previouslySelectedCountry !== selectedCountry) {
        if (selectedCountry) {
            rotateTargetX = selectedCountry.lat * Math.PI / 180;
            var targetY0 = -(selectedCountry.lon - 9) * Math.PI / 180;
            var piCounter = 0;
            while (true) {
                var targetY0Neg = targetY0 - Math.PI * 2 * piCounter;
                var targetY0Pos = targetY0 + Math.PI * 2 * piCounter;
                if (Math.abs(targetY0Neg - rotating.rotation.y) < Math.PI) {
                    rotateTargetY = targetY0Neg;
                    break;
                } else if (Math.abs(targetY0Pos - rotating.rotation.y) < Math.PI) {
                    rotateTargetY = targetY0Pos;
                    break;
                }
                piCounter++;
                rotateTargetY = wrap(targetY0, -Math.PI, Math.PI);
            }
            // console.log(rotateTargetY);
            //lines commented below source of rotation error
            //is there a more reliable way to ensure we don't rotate around the globe too much? 
            /*
             if( Math.abs(rotateTargetY - rotating.rotation.y) > Math.PI )
             rotateTargetY += Math.PI;		
             */
            rotateVX *= 0.6;
            rotateVY *= 0.6;
        }
    }

    d3Graphs.initGraphs();
}