import React, { useEffect, useRef, useState } from 'react';
import mermaid from 'mermaid';

mermaid.initialize({
  startOnLoad: false,
  theme: 'dark',
  securityLevel: 'loose',
  themeVariables: {
    background: '#090d16',
    primaryColor: '#8b5cf6',
    lineColor: '#3b82f6'
  }
});

/**
 * Component that compiles and renders raw Mermaid.js ER diagram strings
 * safely into visual SVG flows.
 */
const MermaidRenderer = ({ chart }) => {
  const containerRef = useRef(null);
  const [svg, setSvg] = useState('');
  const [error, setError] = useState(false);

  useEffect(() => {
    if (!chart) return;
    setError(false);
    
    const renderChart = async () => {
      try {
        const id = `mermaid-${Math.floor(Math.random() * 100000)}`;
        // mermaid.render returns an object containing { svg }
        const { svg: renderedSvg } = await mermaid.render(id, chart);
        setSvg(renderedSvg);
      } catch (err) {
        logMermaidError(err);
        setError(true);
      }
    };

    renderChart();
  }, [chart]);

  const logMermaidError = (err) => {
    console.error("Mermaid render error:", err);
  };

  if (error) {
    return (
      <div style={{ padding: '15px', border: '1px solid var(--border-color)', color: 'var(--text-secondary)', background: 'rgba(239, 68, 68, 0.05)', borderRadius: '8px' }}>
        <span>Failed to render diagram. Raw text:</span>
        <pre style={{ overflowX: 'auto', marginTop: '10px', fontSize: '0.85rem', color: 'var(--text-muted)' }}>{chart}</pre>
      </div>
    );
  }

  return (
    <div 
      ref={containerRef} 
      style={{ 
        overflowX: 'auto', 
        display: 'flex', 
        justifyContent: 'center', 
        background: '#090d16', 
        padding: '20px', 
        borderRadius: '12px', 
        border: '1px solid var(--border-color)',
        minHeight: '200px',
        alignItems: 'center'
      }}
      dangerouslySetInnerHTML={{ __html: svg }} 
    />
  );
};

export default MermaidRenderer;
